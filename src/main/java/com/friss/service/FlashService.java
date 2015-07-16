package com.friss.service;

import java.util.EnumSet;
import java.util.Properties;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.skife.jdbi.v2.DBI;

import io.dropwizard.Application;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

import com.friss.service.config.FlashServiceConfiguration;
import com.friss.service.dao.CommentDAO;
import com.friss.service.dao.PostDAO;
import com.friss.service.post.PostBuilder;
import com.friss.service.resources.CommentResource;
import com.friss.service.resources.PostResource;
import com.friss.service.resources.IndexResource;

public class FlashService extends Application<FlashServiceConfiguration> {

	@Override
	public String getName() {
		return "flash-service";
	}

	public static void main(String[] args) throws Exception {
		new FlashService().run(args);
	}

	@Override
	public void initialize(Bootstrap<FlashServiceConfiguration> bootstrap) {
		bootstrap.addBundle(new SwaggerBundle<FlashServiceConfiguration>() {
			@Override
			public SwaggerBundleConfiguration getSwaggerBundleConfiguration(
					FlashServiceConfiguration configuration) {
				return new SwaggerBundleConfiguration("localhost", 8080);
			}
		});
	}

	@Override
	public void run(FlashServiceConfiguration conf, Environment env)
			throws Exception {

		final FilterRegistration.Dynamic cors = env.servlets().addFilter(
				"CORS", CrossOriginFilter.class);

		// Configure CORS parameters
		cors.setInitParameter("allowedOrigins", "*");
		cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
		cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

		// Add URL mapping
		cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class),
				true, "/*");

		/* Initialize non database settings */
		PostBuilder.ImagePath = conf.getImagePath();
		PostBuilder.MinTimeout = conf.getMinTimeout();
		PostBuilder.MaxTimeout = conf.getMaxTimeout();
		PostBuilder.S3Bucket = conf.getS3Bucket();

		Properties p = new Properties(System.getProperties());
		p.setProperty("aws.accessKeyId", conf.getAWSAccessKey());
		p.setProperty("aws.secretKey", conf.getAWSSecretKey());
		// set the system properties
		System.setProperties(p);

		/* Initialize everything else */
		final DBIFactory factory = new DBIFactory();
		final DBI jdbi = factory.build(env, conf.getDataSourceFactory(), "mysql");

		final PostDAO postDAO = jdbi.onDemand(PostDAO.class);
		final CommentDAO commentDAO = jdbi.onDemand(CommentDAO.class);

		final PostResource postResource = new PostResource(postDAO, commentDAO);
		env.jersey().register(postResource);

		final CommentResource commentResource = new CommentResource(commentDAO,
				postDAO);
		env.jersey().register(commentResource);

		final IndexResource indexResource = new IndexResource();
		env.jersey().register(indexResource);
	}

}
