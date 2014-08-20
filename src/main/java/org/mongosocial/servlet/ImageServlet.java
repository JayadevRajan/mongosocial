package org.mongosocial.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mongosocial.bean.SearchBean;


/*
 * Intercepts all image request and feed them from the searchBean. 
 */
public class ImageServlet extends HttpServlet
{
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			// Get image file.
			String userName = request.getParameter("user");
			SearchBean search = (SearchBean)request.getSession().getAttribute("searchBean");

			if(search == null)
			{
				search = new SearchBean();
				request.getSession().setAttribute("searchBean", search);
			}
			
			byte[] pic = search.getUserPic(userName);
			
			//if image is null just refresh so that search can cache the image. 
			if(pic == null)
				search.refresh(null);
			
			pic = search.getUserPic(userName);
			if(pic == null)
				return;
			
			// Write image contents to response.
			response.getOutputStream().write(pic);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
