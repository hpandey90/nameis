package forsell;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileServlet extends HttpServlet{
	private static final int DEFAULT_BUFFER_SIZE = 10240;
	private String filePath;
	public void init() throws ServletException {
		this.filePath = "\\\\192.168.0.19/uploads/";
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException
	    {
		String requestedFile = request.getPathInfo();
		if (requestedFile == null) {
			 response.sendError(HttpServletResponse.SC_NOT_FOUND);
	         return;
			}	
		 File file = new File(filePath, URLDecoder.decode(requestedFile, "UTF-8"));
		 if (!file.exists()) {
			 response.sendError(HttpServletResponse.SC_NOT_FOUND);
	            return;
		 }		
		 String contentType = getServletContext().getMimeType(file.getName());
		 if (contentType == null) {
	            contentType = "application/octet-stream";
	        }
		 response.reset();
	     response.setBufferSize(DEFAULT_BUFFER_SIZE);
	     response.setContentType(contentType);
	     response.setHeader("Content-Length", String.valueOf(file.length()));
	     response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
	     BufferedInputStream input = null;
	     BufferedOutputStream output = null;
	     try {
	    	 input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
	         output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);
	         byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
	         int length;
	         while ((length = input.read(buffer)) > 0) {
	                output.write(buffer, 0, length);
	         }
	     }
	         finally {
	             close(output);
	             close(input);
	         }
	    }
	
	private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
