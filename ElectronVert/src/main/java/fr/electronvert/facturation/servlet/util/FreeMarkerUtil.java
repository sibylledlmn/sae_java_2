package fr.electronvert.facturation.servlet.util;

import freemarker.cache.WebappTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class FreeMarkerUtil {

    private static Configuration config;

    public static void init(ServletContext servletContext){
        config = new Configuration(Configuration.VERSION_2_3_22);
        config.setTemplateLoader(new WebappTemplateLoader(servletContext,  "/WEB-INF/templates/"));
        config.setDefaultEncoding("UTF-8");
    }

    public static void render(String templateName, Map<String,Object> dataModel, HttpServletResponse response) throws IOException, TemplateException {
        Template template = config.getTemplate(templateName);
        response.setContentType("text/html;charset=UTF-8");
        template.process(dataModel, response.getWriter());
    }

}
