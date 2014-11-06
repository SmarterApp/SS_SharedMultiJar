package AIR.Common.Web.Session;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import AIR.Common.Helpers.CaseInsensitiveMap;
import AIR.Common.Helpers._Ref;
import AIR.Common.Utilities.Path;

public class CaseInsensitiveFileNameFilter implements Filter
{
  private static final Logger _logger        = LoggerFactory.getLogger (CaseInsensitiveFileNameFilter.class);
  private Map<String, String> _pathMap       = Collections.synchronizedMap (new CaseInsensitiveMap<String> ());
  private Map<String, String> _extensionsMap = new CaseInsensitiveMap<String> ();
  private Pattern             _pattern;

  @Override
  public void init (FilterConfig filterConfig) throws ServletException {
    setupExtensionsPattern (filterConfig);
    setUpExtensionMapping (filterConfig);
  }

  @Override
  public void doFilter (ServletRequest wrappedRequest, ServletResponse response, FilterChain chain) throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) wrappedRequest;
    String servletPath = request.getServletPath ();
    String requestedPath = servletPath;

    boolean remapped = false;
    boolean needsSeparateRequestDispatcher = false;

    // first do extensions mapping. this way if we need to take care of casing
    // we will do that in the later step.
    _Ref<String> extensionMappedString = new _Ref<String> ();
    if (getExtensionRemap (servletPath, extensionMappedString))
    {
      servletPath = extensionMappedString.get ();
      remapped = needsSeparateRequestDispatcher = true;
    }
    String remappedServletPath = servletPath;

    // now we can do case insensitive path name mapping.
    if (_pathMap.containsKey (servletPath)) {
      remappedServletPath = _pathMap.get (servletPath);
      remapped = true;
    } else if (isMatchForRewriting (servletPath)) {
      remappedServletPath = remapUri (servletPath);
      if (!StringUtils.equals (remappedServletPath, servletPath)) {
        _pathMap.put (servletPath, remappedServletPath);
        remapped = true;
      }
    }

    if (remapped) {
      final String remappedServletPathFinal = remappedServletPath;
      HttpServletRequestWrapper modifiedHttpRequest = new HttpServletRequestWrapper (request)
      {
        @Override
        public String getServletPath () {
          return remappedServletPathFinal;
        }

        @Override
        public String getRequestURI () {
          return Path.combine (Server.getContextPath (), remappedServletPathFinal, "/");
        }

      };
      request = modifiedHttpRequest;
      if (needsSeparateRequestDispatcher)
      {
        // Sajib/Shiva: This is the only way to handle aspx to xhtml
        // transformations where
        // the xhtml is a real JSF page but this may be expensive as we may
        // already have gone
        // down other filtering chains. We are doing this as a last resort.
        _logger.warn (String.format ("Redispatching as there was an extension transformation. Original URI %s. New URI %s", requestedPath, remappedServletPath));
        request.getRequestDispatcher (request.getRequestURI ()).forward (request, response);
      }
    }

    chain.doFilter (request, response);
  }

  @Override
  public void destroy () {
    // TODO Auto-generated method stub

  }

  /*
   * We will recursively walk starting the context document base
   */
  public static String remapUri (String servletPath) {
    List<String> segments = getPathSegments (servletPath);
    StringBuilder newServletPath = new StringBuilder ();
    if (segments.size () == 1)
      newServletPath = newServletPath.append (segments.get (0));
    else {
      for (String segment : segments) {
        String match = getFile (Server.getDocBasePath () + File.separator + newServletPath.toString (), segment);
        if (match != null)
          newServletPath.append ("/" + match);
      }
    }
    return newServletPath.toString ();
  }

  public static String getFile (String directory, final String filePathSegment) {
    File f = new File (directory);
    if (f.isDirectory ()) {
      String[] filesInFolder = f.list (new FilenameFilter ()
      {
        @Override
        public boolean accept (File dir, String name) {
          if (StringUtils.equalsIgnoreCase (name, filePathSegment))
            return true;
          return false;
        }
      });
      // We are only expecting one match as we are doing a case insensitive
      // match.
      if (filesInFolder != null && filesInFolder.length > 0)
        return filesInFolder[0];
    }
    return filePathSegment;
  }

  private static List<String> getPathSegments (String path) {
    final String[] segments = StringUtils.split (path, "/\\");

    List<String> segmentsList = new ArrayList<String> ();
    if (segments != null && segments.length > 0) {
      for (String segment : segments)
        segmentsList.add (segment);
    }
    return segmentsList;
  }

  private boolean isMatchForRewriting (String path) {
    if (_pattern == null)
      return false;
    return _pattern.matcher (path).matches ();
  }

  private void setupExtensionsPattern (FilterConfig filterConfig)
  {
    String extensions = filterConfig.getInitParameter ("extensionsToFilter");
    if (StringUtils.isEmpty (extensions)) {
      _logger
          .warn ("No extensions provided to filter on. No filtering will be done. Please fill out the extensionsToFilter init-param for CaseInsensitiveFileNameFilter. Extensions are pipe (|) seperated.");
    } else {
      _pattern = Pattern.compile (String.format (".*\\.(?:%s)$", extensions), Pattern.CASE_INSENSITIVE);
    }
  }

  private void setUpExtensionMapping (FilterConfig filterConfig)
  {
    String extensionsMappingConfig = filterConfig.getInitParameter ("extensionsMapping");
    if (StringUtils.isEmpty (extensionsMappingConfig))
    {
      _logger
          .warn ("No extensions mapping provided. Please fill out the extensionsMapping init-param for CaseInsensitiveFileNameFilter. Extensions mapping tuples are pipe (|) seperated e.g. a=b|c=d.... This will changes paths with extensions \"a\" to a path with extension \"b\" and so on.");
    }
    else
    {
      String[] extensionsMapStrings = StringUtils.split (extensionsMappingConfig, "|");
      for (String extensionMapString : extensionsMapStrings)
      {
        String[] pairs = StringUtils.split (extensionMapString, "=");
        if (pairs.length != 2)
        {
          _logger.warn (String.format ("In extensionsMapping filter configuration, %s does not resolve to a key value pair. Skipping.", extensionMapString));
        }
        else
        {
          this._extensionsMap.put (pairs[0], pairs[1]);
        }
      }
    }
  }

  private boolean getExtensionRemap (String servletPath, _Ref<String> remappedServlet)
  {
    String extension = Path.getExtension (servletPath);
    if (this._extensionsMap.containsKey (extension))
    {
      String newExtension = this._extensionsMap.get (extension);
      remappedServlet.set (StringUtils.replace (servletPath, "." + extension, "." + newExtension));
      return true;
    }
    return false;
  }
}
