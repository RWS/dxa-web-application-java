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

        // default location of nodejs
        private const string NodejsDefault = @"C:\Program Files\nodejs\npm.cmd";
        
        //set of files to merge across modules
        private readonly Dictionary<string, List<string>> _mergeFileLines = new Dictionary<string, List<string>>();

        //work folder for unzipping, merging and building design files
        private string _tempFolder;

        public override void Transform(Engine engine, Package package)
        {
            Initialize(engine, package);

            _mergeFileLines.Add("src\\system\\assets\\less\\_custom.less", new List<string>());
            _mergeFileLines.Add("src\\system\\assets\\less\\_modules.less", new List<string>());
            _mergeFileLines.Add("src\\templates\\partials\\module-scripts-header.hbs", new List<string>());
            _mergeFileLines.Add("src\\templates\\partials\\module-scripts-footer.hbs", new List<string>());

            StringBuilder publishedFiles = new StringBuilder();
            string cleanup = package.GetValue("cleanup") ?? String.Empty;

            // not using System.IO.Path.GetTempPath() because the paths in our zip are already quite long,
            // so we need a very short temp path for the extract of our zipfile to succeed
            // using drive from tridion cm homedir for temp folder
            _tempFolder = ConfigurationSettings.GetTcmHomeDirectory().Substring(0, 3) + "t" + DateTime.Now.ToString("yyyyMMddHHmmssfff");

            try
            {
                // read values from Component
                var config = GetComponent();
                var fields = new ItemFields(config.Content, config.Schema);
                var favicon = fields.GetMultimediaLink("favicon");
                var version = fields.GetTextValue("version");
                var nodeJs = fields.GetTextValue("nodeJs");

                // set defaults if required
                if (String.IsNullOrEmpty(nodeJs))
                {
                    nodeJs = NodejsDefault;
                }

                PublishJson(String.Format("{{\"version\":{0}}}", JsonEncode(version)), config, GetPublication().RootStructureGroup, "version", "version");

                // create temp folder
                Directory.CreateDirectory(_tempFolder);
                Log.Debug("Created " + _tempFolder);
                
                ProcessModules();                
                
                // build html design
                ProcessStartInfo info = new ProcessStartInfo
                    {
                        FileName = "cmd.exe",
                        Arguments = String.Format("/c \"{0}\" start --color=false", nodeJs),
                        WorkingDirectory = _tempFolder,
                        CreateNoWindow = true,
                        ErrorDialog = false,
                        UseShellExecute = false,
                        RedirectStandardOutput = true,
                        RedirectStandardError = true,
                        StandardErrorEncoding = Encoding.UTF8,
                        StandardOutputEncoding = Encoding.UTF8
                    };
                using (Process cmd = new Process {StartInfo = info})
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
                            string user = System.Security.Principal.WindowsIdentity.GetCurrent().Name;
                            Exception ex = new Exception(error);
                            ex.Data.Add("Filename", info.FileName);
                            ex.Data.Add("Arguments", info.Arguments);
                            ex.Data.Add("User", user);

                            if (error.ToLower().Contains("the system cannot find the path specified"))
                            {
                                throw new Exception(String.Format("Node.js not installed or missing from path for user {0}.", user), ex);
                            }
                            else if (error.ToLower().Contains("mkdir") && error.ToLower().Contains("appdata\\roaming\\npm"))
                            {
                                throw new Exception(String.Format("Node.js cannot access %APPDATA% for user {0}.", user), ex);
                            }

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
                    foreach (var file in files)
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
                            var binary = engine.PublishingContext.RenderedItem.AddBinary(binaryItem.GetAsStream(), filename, sg, "dist-" + filename, config, GetMimeType(extension));
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
            var modules = GetActiveModules();
            foreach (var module in modules)
            {
                var moduleName = module.Key;
                if (moduleName != "core")
                {
                    ProcessModule(moduleName, module.Value);
                }
            }
            ProcessModule("core", modules["core"]);

            //overwrite all merged files
            foreach (var mergeFile in _mergeFileLines)
            {
                string file = Path.Combine(_tempFolder, mergeFile.Key);
                File.WriteAllText(file, String.Join(Environment.NewLine, mergeFile.Value));
                Log.Debug("Saved " + file);
            }   
        }

        protected void ProcessModule(string moduleName, Component moduleComponent)
        {
            var designConfig = GetDesignConfigComponent(moduleComponent);
            if (designConfig != null)
            {
                var fields = new ItemFields(designConfig.Content, designConfig.Schema);
                var zip = fields.GetComponentValue("design");
                var variables = fields.GetComponentValue("variables");
                var code = fields.GetTextValue("code");
                var customLess = GetModuleCustomLess(variables, code);
                if (zip != null)
                {

                    string zipfile = Path.Combine(_tempFolder, moduleName + "-html-design.zip");
                    File.WriteAllBytes(zipfile, zip.BinaryContent.GetByteArray());
                    using (var archive = ZipFile.OpenRead(zipfile))
                    {
                        archive.ExtractToDirectory(_tempFolder, true);
                    }
                    List<string> files = _mergeFileLines.Keys.Select(s => s).ToList();
                    foreach (var mergeFile in files)
                    {
                        //var path = _tempFolder + mergeFile;
                        var path = Path.Combine(_tempFolder, mergeFile);
                        if (File.Exists(path))
                        {
                            foreach (var line in File.ReadAllLines(path))
                            {
                                if (!_mergeFileLines[mergeFile].Contains(line.Trim()))
                                {
                                    _mergeFileLines[mergeFile].Add(line.Trim());
                                }
                            }
                        }
                    }
                    if (!String.IsNullOrEmpty(customLess.Trim()))
                    {
                        _mergeFileLines["src\\system\\assets\\less\\_custom.less"].Add(customLess);
                    }
                }

                if (moduleName.Equals("core"))
                {
                    var buildFiles = fields.GetComponentValue("build");
                    if (buildFiles != null)
                    {
                        ProcessBuildFiles(buildFiles);
                    }
                }
            }
        }

        protected void ProcessBuildFiles(Component zip)
        {
            string zipfile = Path.Combine(_tempFolder, "build-files.zip");
            File.WriteAllBytes(zipfile, zip.BinaryContent.GetByteArray());
            using (var archive = ZipFile.OpenRead(zipfile))
            {
                archive.ExtractToDirectory(_tempFolder, true);
            }
        }

        private static Component GetDesignConfigComponent(Component moduleComponent)
        {
            var fields = new ItemFields(moduleComponent.Content, moduleComponent.Schema);
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
                var itemFields = new ItemFields(variables.Content, variables.Schema);
                foreach (var itemField in itemFields)
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
