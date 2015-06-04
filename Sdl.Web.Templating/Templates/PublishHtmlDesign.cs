using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.IO.Compression;
using System.Linq;
using System.Text;
using Sdl.Web.Tridion.Common;
using Tridion.ContentManager;
using Tridion.ContentManager.CommunicationManagement;
using Tridion.ContentManager.ContentManagement;
using Tridion.ContentManager.ContentManagement.Fields;
using Tridion.ContentManager.Publishing.Rendering;
using Tridion.ContentManager.Templating;
using Tridion.ContentManager.Templating.Assembly;

namespace Sdl.Web.Tridion.Templates
{
    /// <summary>
    /// Publish HTML design by unpacking the templates and less variables and running grunt to build it.
    /// </summary>
    [TcmTemplateTitle("Publish HTML Design")]
    [TcmTemplateParameterSchema("resource:Sdl.Web.Tridion.Resources.PublishHtmlDesignParameters.xsd")]
    public class PublishHtmlDesign : TemplateBase
    {
        // template builder log
        private static readonly TemplatingLogger Log = TemplatingLogger.GetLogger(typeof(PublishHtmlDesign));

        // name of system structure group
        private const string SystemSgName = "_System";
 
        // json content in page
        private const string JsonOutputFormat = "{{\"status\":\"Success\",\"files\":[{0}]}}";
        
        // set of files to merge across modules
        private readonly Dictionary<string, List<string>> _mergeFileLines = new Dictionary<string, List<string>>();

        // work folder for unzipping, merging and building design files
        private string _tempFolder;

        public override void Transform(Engine engine, Package package)
        {
            Initialize(engine, package);

            _mergeFileLines.Add("src\\system\\assets\\less\\_custom.less", new List<string>());
            _mergeFileLines.Add("src\\system\\assets\\less\\_modules.less", new List<string>());
            _mergeFileLines.Add("src\\templates\\partials\\module-scripts-header.hbs", new List<string>());
            _mergeFileLines.Add("src\\templates\\partials\\module-scripts-footer.hbs", new List<string>());

            StringBuilder publishedFiles = new StringBuilder();
            string drive = package.GetValue("drive") ?? String.Empty;
            string cleanup = package.GetValue("cleanup") ?? String.Empty;

            // not using System.IO.Path.GetTempPath() because the paths in our zip are already quite long,
            // so we need a very short temp path for the extract of our zipfile to succeed
            // using current time and convert to hex (the date won't matter as this folder is cleaned up so time is unique enough)
            int timestamp = Convert.ToInt32(DateTime.Now.ToString("HHmmssfff"));
            if (!String.IsNullOrEmpty(drive) && Char.IsLetter(drive.First()))
            {

                _tempFolder = drive.First() + @":\_" + timestamp.ToString("x");
            }
            else
            {
                // using drive from tridion cm homedir for temp folder
                _tempFolder = ConfigurationSettings.GetTcmHomeDirectory().Substring(0, 3) + "_" + timestamp.ToString("x");
            }

            try
            {
                // read values from Component
                Component config = GetComponent();
                ItemFields fields = new ItemFields(config.Content, config.Schema);
                Component favicon = fields.GetMultimediaLink("favicon");
                string version = fields.GetTextValue("version");

                PublishJson(String.Format("{{\"version\":{0}}}", JsonEncode(version)), config, GetPublication().RootStructureGroup, "version", "version");

                // create temp folder
                Directory.CreateDirectory(_tempFolder);
                Log.Debug("Created " + _tempFolder);
                
                // unzip and merge files
                ProcessModules();                
                
                // build html design
                string user = System.Security.Principal.WindowsIdentity.GetCurrent().Name;
                ProcessStartInfo info = new ProcessStartInfo
                    {
                        FileName = "cmd.exe",
                        Arguments = "/c npm start --color=false",
                        WorkingDirectory = _tempFolder,
                        CreateNoWindow = true,
                        ErrorDialog = false,
                        UseShellExecute = false,
                        RedirectStandardOutput = true,
                        RedirectStandardError = true,
                        StandardErrorEncoding = Encoding.UTF8,
                        StandardOutputEncoding = Encoding.UTF8
                    };
                using (Process cmd = new Process { StartInfo = info })
                {
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
                            Exception ex = new Exception(error);
                            ex.Data.Add("Filename", info.FileName);
                            ex.Data.Add("Arguments", info.Arguments);
                            ex.Data.Add("User", user);

                            // TODO: check for known errors and throw exception with a user friendly message
                            //if (error.ToLower().Contains("something"))
                            //{
                            //    throw new Exception(String.Format("Something went wrong for user {0}.", user), ex);
                            //}

                            throw ex;
                        }
                    }
                    cmd.WaitForExit();
                }

                // publish all binaries from dist folder
                string dist = Path.Combine(_tempFolder, "dist");
                if (Directory.Exists(dist))
                {
                    // save favicon to disk (if available)
                    if (favicon != null)
                    {
                        string ico = Path.Combine(dist, "favicon.ico");
                        File.WriteAllBytes(ico, favicon.BinaryContent.GetByteArray());
                        Log.Debug("Saved " + ico);
                    }

                    string[] files = Directory.GetFiles(dist, "*.*", SearchOption.AllDirectories);
                    foreach (string file in files)
                    {
                        string filename = Path.GetFileName(file);
                        string extension = Path.GetExtension(file);
                        Log.Debug("Found " + file);

                        // determine correct structure group
                        Publication pub = (Publication)config.ContextRepository;
                        string relativeFolderPath = file.Substring(dist.Length, file.LastIndexOf('\\') - dist.Length);
                        Log.Debug("Relative path: " + relativeFolderPath);
                        relativeFolderPath = relativeFolderPath.Replace("system", SystemSgName).Replace('\\', '/');
                        string pubSgWebDavUrl = pub.RootStructureGroup.WebDavUrl;
                        string publishSgWebDavUrl = pubSgWebDavUrl + relativeFolderPath;
                        Log.Debug("Structure Group WebDAV URL: " + publishSgWebDavUrl);
                        StructureGroup sg = engine.GetObject(publishSgWebDavUrl) as StructureGroup;
                        if (sg == null)
                        {
                            throw new Exception("Missing Structure Group " + publishSgWebDavUrl);
                        }

                        // add binary to package and publish
                        using (FileStream fs = File.OpenRead(file))
                        {
                            Item binaryItem = Package.CreateStreamItem(GetContentType(extension), fs);
                            Binary binary = engine.PublishingContext.RenderedItem.AddBinary(binaryItem.GetAsStream(), filename, sg, "dist-" + filename, config, GetMimeType(extension));
                            binaryItem.Properties[Item.ItemPropertyPublishedPath] = binary.Url;
                            package.PushItem(filename, binaryItem);
                            if (publishedFiles.Length > 0)
                            {
                                publishedFiles.Append(",");
                            }
                            publishedFiles.AppendFormat("\"{0}\"", binary.Url);
                            Log.Info("Published " + binary.Url);
                        }                            
                    }
                }
                else
                {
                    throw new Exception("Grunt build failed, dist folder is missing.");
                }
            }
            finally
            {
                if (String.IsNullOrEmpty(cleanup) || !cleanup.ToLower().Equals("false"))
                {
                    // cleanup workfolder
                    Directory.Delete(_tempFolder, true);
                    Log.Debug("Removed " + _tempFolder);
                }
                else
                {
                    Log.Debug("Did not cleanup " + _tempFolder);
                }
            }
            // output json result
            package.PushItem(Package.OutputName, package.CreateStringItem(ContentType.Text, String.Format(JsonOutputFormat, publishedFiles)));
        }

        protected void ProcessModules()
        {
            Dictionary<string, Component> modules = GetActiveModules();
            foreach (KeyValuePair<string, Component> module in modules)
            {
                string moduleName = module.Key;
                if (moduleName != "core")
                {
                    ProcessModule(moduleName, module.Value);
                }
            }
            ProcessModule("core", modules["core"]);

            // overwrite all merged files
            foreach (KeyValuePair<string, List<string>> mergeFile in _mergeFileLines)
            {
                string file = Path.Combine(_tempFolder, mergeFile.Key);
                File.WriteAllText(file, String.Join(Environment.NewLine, mergeFile.Value));
                Log.Debug("Saved " + file);
            }   
        }

        protected void ProcessModule(string moduleName, Component moduleComponent)
        {
            Component designConfig = GetDesignConfigComponent(moduleComponent);
            if (designConfig != null)
            {
                ItemFields fields = new ItemFields(designConfig.Content, designConfig.Schema);
                Component zip = fields.GetComponentValue("design");
                Component variables = fields.GetComponentValue("variables");
                string code = fields.GetTextValue("code");
                string customLess = GetModuleCustomLess(variables, code);
                if (zip != null)
                {
                    // write binary contents as zipfile to disk
                    string zipfile = Path.Combine(_tempFolder, moduleName + "-html-design.zip");
                    File.WriteAllBytes(zipfile, zip.BinaryContent.GetByteArray());

                    // unzip
                    using (ZipArchive archive = ZipFile.OpenRead(zipfile))
                    {
                        archive.ExtractToDirectory(_tempFolder, true);
                    }

                    // add content from merge files if available
                    List<string> files = _mergeFileLines.Keys.Select(s => s).ToList();
                    foreach (string mergeFile in files)
                    {
                        string path = Path.Combine(_tempFolder, mergeFile);
                        if (File.Exists(path))
                        {
                            foreach (string line in File.ReadAllLines(path))
                            {
                                if (!_mergeFileLines[mergeFile].Contains(line.Trim()))
                                {
                                    _mergeFileLines[mergeFile].Add(line.Trim());
                                }
                            }
                        }
                    }

                    // add custom less code block
                    if (!String.IsNullOrEmpty(customLess.Trim()))
                    {
                        _mergeFileLines["src\\system\\assets\\less\\_custom.less"].Add(customLess);
                    }
                }

                if (moduleName.Equals("core"))
                {
                    // unzip build files (nodejs, npm and grunt etc.)
                    Component buildFiles = fields.GetComponentValue("build");
                    if (buildFiles != null)
                    {
                        ProcessBuildFiles(buildFiles);
                    }
                }
            }
        }

        protected void ProcessBuildFiles(Component zip)
        {
            // write binary contents as zipfile to disk
            string zipfile = Path.Combine(_tempFolder, "build-files.zip");
            File.WriteAllBytes(zipfile, zip.BinaryContent.GetByteArray());

            // unzip
            using (ZipArchive archive = ZipFile.OpenRead(zipfile))
            {
                archive.ExtractToDirectory(_tempFolder, true);
            }
        }

        private static Component GetDesignConfigComponent(Component moduleComponent)
        {
            ItemFields fields = new ItemFields(moduleComponent.Content, moduleComponent.Schema);
            return fields.GetComponentValue("designConfiguration");
        }

        private static string GetModuleCustomLess(Component variables, string code)
        {
            const string line = "@{0}: {1};";
            StringBuilder content = new StringBuilder();

            // save less variables to disk (if available) in unpacked zip structure
            if (variables != null)
            {
                // assuming all fields are text fields with a single value
                ItemFields itemFields = new ItemFields(variables.Content, variables.Schema);
                foreach (ItemField itemField in itemFields)
                {
                    string value = ((TextField)itemField).Value;
                    if (!String.IsNullOrEmpty(value))
                    {
                        content.AppendFormat(line, itemField.Name, ((TextField)itemField).Value);
                    }
                }
            }
            if (code != null)
            {
                content.Append(code);
            }
            return content.ToString();
        }

        private static ContentType GetContentType(string extension)
        {
            // remove dot if extension starts with it
            if (extension.StartsWith("."))
            {
                extension = extension.Substring(1);
            }

            switch (extension)
            {
                case "css":
                case "js":
                case "htc":
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
            // remove dot if extension starts with it
            if (extension.StartsWith("."))
            {
                extension = extension.Substring(1);
            }

            switch (extension)
            {
                case "css":
                    return "text/css";
                case "js":
                    return "application/x-javascript";
                case "htc":
                    return "text/x-component";
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
