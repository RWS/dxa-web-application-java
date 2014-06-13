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
        public override void Transform(Engine engine, Package package)
        {
            Initialize(engine, package);
            StringBuilder messages = new StringBuilder();

            try
            {
                // read values from component
                var config = GetComponent();
                var fields = new ItemFields(config.Content, config.Schema);
                var design = fields.GetMultimediaLink("design");
                string workFolder = fields.GetTextValue("workfolder");
                if (!workFolder.EndsWith("\\"))
                {
                    workFolder = workFolder + "\\";
                }

                // create temp workfolder
                string timestamp = DateTime.Now.ToString("yyyyMMddHHmmss"); ;
                string tempFolder = workFolder + "tmp" + timestamp + "\\";
                Directory.CreateDirectory(tempFolder);

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

                Process cmd = new Process { StartInfo = info };
                cmd.Start();
                using (StreamReader reader = cmd.StandardOutput)
                {
                    string output = reader.ReadToEnd();
                    if (!String.IsNullOrEmpty(output))
                    {
                        messages.AppendLine("Grunt output:");
                        messages.AppendLine(output);
                    }
                }
                using (StreamReader reader = cmd.StandardError)
                {
                    string error = reader.ReadToEnd();
                    if (!String.IsNullOrEmpty(error))
                    {
                        messages.AppendLine("Grunt errors:");
                        messages.AppendLine(error);
                    }
                }
                cmd.WaitForExit();
                cmd.Close();

                // publish all binaries from dist folder
                string dist = tempFolder + "dist\\";
                if (Directory.Exists(dist))
                {
                    string[] files = Directory.GetFiles(dist, "*.*", SearchOption.AllDirectories);
                    messages.AppendLine("");
                    messages.AppendLine("Files:");

                    foreach (var file in files)
                    {
                        string filename = file.Substring(file.LastIndexOf('\\') + 1);
                        string extension = filename.Substring(filename.LastIndexOf('.') + 1);
                        messages.AppendLine(file);

                        // determine correct structure group
                        Publication pub = (Publication)config.ContextRepository;
                        string relativeFolderPath = file.Substring(dist.Length - 1, file.LastIndexOf('\\') + 1 - dist.Length);
                        relativeFolderPath = relativeFolderPath.Replace("system", "_System").Replace('\\', '/');
                        string pubSgWebDavUrl = pub.RootStructureGroup.WebDavUrl;
                        string publishSgWebDavUrl = pubSgWebDavUrl + relativeFolderPath;
                        StructureGroup sg = engine.GetObject(publishSgWebDavUrl) as StructureGroup;
                        if (sg == null)
                        {
                            messages.AppendLine("The structure group mirroring the asset folder does not exist.  SG=" + publishSgWebDavUrl);
                        }
                        else 
                        {
                            using (FileStream fs = File.OpenRead(file))
                            {
                                // add binary to package and publish
                                Item binaryItem = Package.CreateStreamItem(GetContentType(extension), fs);
                                var binary = engine.PublishingContext.RenderedItem.AddBinary(binaryItem.GetAsStream(), filename, sg, "dist-" + filename, config, GetMimeType(extension));
                                binaryItem.Properties[Item.ItemPropertyPublishedPath] = binary.Url;
                                //package.PushItem(filename, binaryItem);
                                package.PushItem(binary.Url, binaryItem);
                            }
                        }
                    }
                }

                // cleanup workfolder
                Directory.Delete(tempFolder, true);
            }
            catch (Exception ex)
            {
                messages.AppendLine(ex.Message);
            }

            // output messages
            package.PushItem(Package.OutputName, package.CreateStringItem(ContentType.Text, messages.ToString()));
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
