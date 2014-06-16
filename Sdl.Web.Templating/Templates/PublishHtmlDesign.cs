using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.IO.Compression;
using System.Linq;
using System.Security.Principal;
using System.Text;
using System.Web.Helpers;
using Sdl.Web.Tridion.Common;
using Tridion.ContentManager;
using Tridion.ContentManager.CommunicationManagement;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.ContentManagement.Fields;
using Tridion.ContentManager.Templating;
using Tridion.ContentManager.Templating.Assembly;

namespace Sdl.Web.Tridion.Templates
{
    /// <summary>
    /// Publish HTML design by unpacking the templates and less variables and running grunt to build it.
    /// </summary>
    [TcmTemplateTitle("Publish HTML Design")]
    public class PublishHtmlDesign : TemplateBase
    {
        // template builder log
        private static readonly TemplatingLogger Log = TemplatingLogger.GetLogger(typeof(PublishHtmlDesign));

        private const string SystemSgName = "_System";
 
        // json content in page
        private const string Json = "{{\"status\":\"Success\",\"files\":[{0}]}}";

        public override void Transform(Engine engine, Package package)
        {
            Initialize(engine, package);
            StringBuilder publishedFiles = new StringBuilder();

            // using drive from tridion cm homedir for temp folder
            string tempFolder = ConfigurationSettings.GetTcmHomeDirectory().Substring(0, 3) + "tmp" + DateTime.Now.ToString("yyyyMMddHHmmss") + "\\";

            try
            {
                // read values from component
                var config = GetComponent();
                var fields = new ItemFields(config.Content, config.Schema);
                var design = fields.GetMultimediaLink("design");
                var favicon = fields.GetMultimediaLink("favicon");

                // create temp folder
                Directory.CreateDirectory(tempFolder);
                Log.Debug("Created " + tempFolder);

                // save zipfile to disk and unpack
                string zipfile = tempFolder + "html-design.zip";
                File.WriteAllBytes(zipfile, design.BinaryContent.GetByteArray());
                ZipFile.ExtractToDirectory(zipfile, tempFolder);

                // build html design
                ProcessStartInfo info = new ProcessStartInfo
                    {
                        FileName = "cmd.exe",
                        Arguments = @"/c c:\progra~1\nodejs\npm.cmd test --color=false",
                        WorkingDirectory = tempFolder,
                        CreateNoWindow = true,
                        ErrorDialog = false,
                        UseShellExecute = false,
                        RedirectStandardOutput = true,
                        RedirectStandardError = true,
                        StandardErrorEncoding = Encoding.UTF8,
                        StandardOutputEncoding = Encoding.UTF8
                    };

                Process cmd = new Process {StartInfo = info};
                cmd.Start();
                using (StreamReader reader = cmd.StandardOutput)
                {
                    string output = reader.ReadToEnd();
                    if (!String.IsNullOrEmpty(output))
                    {
                        Log.Info(output);

                        // TODO: check for errors in standard output and throw exception
                    }
                }
                using (StreamReader reader = cmd.StandardError)
                {
                    string error = reader.ReadToEnd();
                    if (!String.IsNullOrEmpty(error))
                    {
                        Log.Error(error);

                        // TODO: throw readable exception
                        //throw new Exception(error);
                    }
                }
                cmd.WaitForExit();
                cmd.Close();

                // publish all binaries from dist folder
                string dist = tempFolder + "dist\\";
                if (Directory.Exists(dist))
                {
                    // save favicon to disk (if available)
                    if (favicon != null)
                    {
                        File.WriteAllBytes(dist + "favicon.ico", favicon.BinaryContent.GetByteArray());
                    }

                    string[] files = Directory.GetFiles(dist, "*.*", SearchOption.AllDirectories);
                    foreach (var file in files)
                    {
                        string filename = file.Substring(file.LastIndexOf('\\') + 1);
                        string extension = filename.Substring(filename.LastIndexOf('.') + 1);
                        Log.Debug("Found " + file);

                        // determine correct structure group (create if not exists)
                        Publication pub = (Publication)config.ContextRepository;
                        string relativeFolderPath = file.Substring(dist.Length - 1, file.LastIndexOf('\\') + 1 - dist.Length);
                        relativeFolderPath = relativeFolderPath.Replace("system", SystemSgName).Replace('\\', '/');
                        string pubSgWebDavUrl = pub.RootStructureGroup.WebDavUrl;
                        string publishSgWebDavUrl = pubSgWebDavUrl + relativeFolderPath;
                        StructureGroup sg = engine.GetObject(publishSgWebDavUrl) as StructureGroup;
                        if (sg == null)
                        {
                            string sgAfterSystem = publishSgWebDavUrl.Substring(publishSgWebDavUrl.IndexOf(SystemSgName, StringComparison.Ordinal) + SystemSgName.Length);
                            CreateStructure(engine, sgAfterSystem.Split(new[] {'/'}, StringSplitOptions.RemoveEmptyEntries), pubSgWebDavUrl + "/" + SystemSgName);
                        }

                        // add binary to package and publish
                        using (FileStream fs = File.OpenRead(file))
                        {
                            Item binaryItem = Package.CreateStreamItem(GetContentType(extension), fs);
                            var binary = engine.PublishingContext.RenderedItem.AddBinary(binaryItem.GetAsStream(), filename, sg, "dist-" + filename, config, GetMimeType(extension));
                            binaryItem.Properties[Item.ItemPropertyPublishedPath] = binary.Url;
                            package.PushItem(filename, binaryItem);
                            if (publishedFiles.Length > 0)
                            {
                                publishedFiles.Append(",");
                            }
                            publishedFiles.AppendFormat("\"{0}\"", binary.Url);
                            Log.Debug("Published " + binary.Url);
                        }
                    }
                }
                else
                {
                    Log.Error("Grunt build failed, dist folder missing.");
                }
            }
            catch (Exception ex)
            {
                Log.Error(ex.Message);

                // TODO: throw readable exception for know cases (nodejs not installed etc.)
                throw new Exception(ex.Message, ex);
            }
            finally
            {
                // cleanup workfolder
                Directory.Delete(tempFolder, true);
                Log.Debug("Removed " + tempFolder);                
            }

            // output json result
            package.PushItem(Package.OutputName, package.CreateStringItem(ContentType.Text, String.Format(Json, publishedFiles)));
        }

        private static void CreateStructure(Engine engine, IEnumerable<string> structureGroups, string webDavUrlSystemSg)
        {
            StringBuilder sgs = new StringBuilder();
            StringBuilder parentUrl = new StringBuilder();
            parentUrl.Append(webDavUrlSystemSg);

            foreach (var sgName in structureGroups)
            {
                sgs.Append("/" + sgName);
                StructureGroup sg = engine.GetObject(webDavUrlSystemSg + sgs) as StructureGroup;
                if (sg == null)
                {
                    StructureGroup parent = (StructureGroup)engine.GetObject(parentUrl.ToString());
                    sg = new StructureGroup(engine.GetSession(), parent.Id) { Title = sgName, Directory = sgName };
                    sg.Save();
                    Log.Info("Created " + webDavUrlSystemSg + sgs);
                }
                parentUrl.Append("/" + sgName);
            }
        }

        private static ContentType GetContentType(string extension)
        {
            switch (extension)
            {
                case "css":
                case "js":
                    return ContentType.Text;
                case "gif":
                    return ContentType.Gif;
                case "jpg":
                case "jpeg":
                case "jpe":
                    return ContentType.Jpeg;
                case "ico":
                case "png":
                    return ContentType.Png;
                default:
                    return ContentType.Unknown;
            }
        }

        private static string GetMimeType(string extension)
        {
            switch (extension)
            {
                case "css":
                    return "text/css";
                case "js":
                    return "application/x-javascript";
                case "gif":
                    return "image/gif";
                case "jpg":
                case "jpeg":
                case "jpe":
                    return "image/jpeg";
                case "ico":
                    return "image/x-icon";
                case "png":
                    return "image/png";
                case "svg":
                    return "image/svg+xml";
                case "eot":
                    return "application/vnd.ms-fontobject";
                case "woff":
                    return "application/x-woff";
                case "otf":
                    return "application/x-font-opentype";
                case "ttf":
                    return "application/x-font-ttf";
                default:
                    return "application/octet-stream";
            }
        }
    }
}
