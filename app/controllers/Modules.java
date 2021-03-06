package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import models.Module;
import models.Version;

import org.yaml.snakeyaml.Yaml;

import play.Logger;
import play.Play;
import play.libs.IO;
import play.mvc.Controller;
import play.utils.Properties;
import util.Textile;

public class Modules extends Controller {
    
    public static void index(String id) throws FileNotFoundException {

        String action = "modules";

        Module module = getModule(id);
        List<Version> versions = module.versions;

        render(action, module, versions);
    }
    
    public static void page(String id, String version) throws IOException {

        String action = "modules";

        Module module = getModule(id);
        List<Version> versions = module.versions;

        String basepath = getVersionPath(id, version);

        File manifest = new File(basepath, "/manifest");

        Properties prop = new Properties();
        prop.load(new FileInputStream(manifest));

        String frameworkVersions = prop.get("frameworkVersions");

        File page = new File(basepath, "documentation/manual/home.textile");
        if (!page.exists()) {
            notFound(page.getPath());
        }
        String html = Textile.toHTML(IO.readContentAsString(page));

        render(action, id, version, frameworkVersions, versions, html);
    }
    
    public static void image(String id, String version, String name) {
        File image = new File(getVersionPath(id, version) + "/documentation/images/" + name + ".png");
        if (!image.exists()) {
            notFound();
        }
        renderBinary(image);
    }
    
    static Module getModule(String id) throws FileNotFoundException {

        File manifest = new File(getModulePath(id) + "/manifest");

        Map<String, Object> map = (Map<String, Object>) new Yaml().load(new FileInputStream(manifest));

        return new Module(map);
    }
    
    static String getModulePath(String id) {
        
        StringBuilder builder = new StringBuilder();
        builder.append(Play.applicationPath);
        builder.append("/documentation/modules/");
        builder.append(id);
        
        return builder.toString();
    }
    
    static String getVersionPath(String id, String version) {

        StringBuilder builder = new StringBuilder();
        builder.append(getModulePath(id));
        builder.append("/");
        builder.append(id);
        builder.append("-");
        builder.append(version);
        
        return builder.toString();
    }
}