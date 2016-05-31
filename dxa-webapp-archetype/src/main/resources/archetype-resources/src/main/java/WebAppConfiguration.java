package $package;

import com.sdl.dxa.DxaSpringInitialization;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import static com.sdl.webapp.common.util.InitializationUtils.loadActiveSpringProfiles;
import static com.sdl.webapp.common.util.InitializationUtils.registerListener;
import static com.sdl.webapp.common.util.InitializationUtils.registerServlet;

@Configuration
@ComponentScan(basePackages = "$groupId")
@Import(DxaSpringInitialization.class)
public class WebAppConfiguration implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext servletAppContext = new AnnotationConfigWebApplicationContext();

        servletAppContext.register(DxaSpringInitialization.class);

        registerListener(servletContext, new ContextLoaderListener(servletAppContext));
        registerServlet(servletContext, new DispatcherServlet(servletAppContext), "/").setLoadOnStartup(1);

        loadActiveSpringProfiles(servletContext, servletAppContext);
    }
}
