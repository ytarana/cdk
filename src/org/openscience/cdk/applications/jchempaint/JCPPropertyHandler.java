/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2005  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.jchempaint;

import java.io.*;
import java.util.*;
import java.net.*;

import org.openscience.cdk.tools.LoggingTool;

/**
 *  A property manager for JChemPaint.
 *
 * @cdk.module jchempaint
 * @author     steinbeck
 * @created    22. April 2005
 */
public class JCPPropertyHandler
{

	private static JCPPropertyHandler jcpPropsHandler = null;

	private LoggingTool logger;
	private Properties currentProperties;
	private File defaultPropsFile;
	private File userPropsFile;
	private File userAtypeFile;
	private File uhome;
	private File ujcpdir;
	private ResourceBundle guiDefinition;
	private ResourceBundle resources;


	/**
	 *  Constructor for the JCPPropertyHandler object
	 */
	private JCPPropertyHandler()
	{
		logger = new LoggingTool(this);
		currentProperties = null;
		defaultPropsFile = null;
		userPropsFile = null;
		userAtypeFile = null;
		uhome = null;
		ujcpdir = null;
		guiDefinition = null;
	}


	/**
	 *  Gets the instance attribute of the JCPPropertyHandler class
	 *
	 *@return    The instance value
	 */
	public static JCPPropertyHandler getInstance()
	{
		if (jcpPropsHandler == null)
		{
			jcpPropsHandler = new JCPPropertyHandler();
		}
		return jcpPropsHandler;
	}


	/**
	 *  Gets the jCPProperties attribute of the JCPPropertyHandler object
	 *
	 *@return    The jCPProperties value
	 */
	public Properties getJCPProperties()
	{
		if (currentProperties == null)
		{
			reloadProperties();
		}
		return currentProperties;
	}


	/**
	 *  Description of the Method
	 */
	public void reloadProperties()
	{
		Properties applicationProps = null;
		Properties defaultProps = null;
		InputStream defaultStream;
		try
		{
			defaultStream = JCPPropertyHandler.class.getResourceAsStream("resources/JChemPaintResources.properties");
			defaultProps = new Properties();
			defaultProps.load(defaultStream);
			defaultStream.close();
			logger.info("Loaded properties from jar");
		} catch (Exception exception)
		{
			logger.error("There was a problem retrieving JChemPaint's default properties.");
			logger.debug(exception);
		}

		try
		{
			// set up real properties
			applicationProps = new Properties(defaultProps);
			FileInputStream appStream = new FileInputStream(getUserPropsFile());
			applicationProps.load(appStream);
			appStream.close();
			logger.info("Loaded user properties from file");
		} catch (FileNotFoundException exception)
		{
			logger.warn("User does not have localized properties in ");
		} catch (Exception exception)
		{
			logger.error("There was a problem retrieving the user properties from file");
			logger.debug(exception);
		}
		currentProperties = applicationProps;
	}


	/**
	 *  Description of the Method
	 */
	public void saveProperties()
	{
		try
		{
			FileOutputStream appStream = new FileOutputStream(getUserPropsFile());
			currentProperties.store(appStream, null);
			appStream.flush();
			appStream.close();
			logger.info("Properties save to ", getUserPropsFile());
		} catch (Exception exception)
		{
			logger.error("An error has occured while storing properties");
			logger.error("to file ");
			logger.debug(exception);
		}
	}


	/**
	 *  Gets the userHome attribute of the JCPPropertyHandler object
	 *
	 *@return    The userHome value
	 */
	public File getUserHome()
	{
		if (uhome == null)
		{
			try
			{
				uhome = new File(System.getProperty("user.home"));
			} catch (Exception exc)
			{
				logger.error("Could not read a system property. Failing!");
				logger.debug(exc);
			}
		}
		return uhome;
	}


	/**
	 *  Gets the jChemPaintDir attribute of the JCPPropertyHandler object
	 *
	 *@return    The jChemPaintDir value
	 */
	public File getJChemPaintDir()
	{
		if (ujcpdir == null)
		{
			try
			{
				ujcpdir = new File(getUserHome(), ".jchempaint");
				ujcpdir.mkdirs();
			} catch (Exception exc)
			{
				logger.error("Could read a JChemPaint dir. I might be in a sandbox.");
				logger.debug(exc);
			}
		}
		return ujcpdir;
	}


	/**
	 *  Gets the userPropsFile attribute of the JCPPropertyHandler object
	 *
	 *@return    The userPropsFile value
	 */
	public File getUserPropsFile()
	{
		if (userPropsFile == null)
		{
			try
			{
				userPropsFile = new File(getJChemPaintDir(), "properties");
			} catch (Exception exc)
			{
				logger.error("Could not read a system property. I might be in a sandbox.");
				logger.debug(exc);
			}
		}
		return userPropsFile;
	}


	/**
	 *  Gets the gUIDefinition attribute of the JCPPropertyHandler object
	 *
	 *@return    The gUIDefinition value
	 */
	public ResourceBundle getGUIDefinition()
	{
		if (guiDefinition == null)
		{
			String guiString = null;
			try
			{
				guiString = System.getProperty("gui");
			} catch (Exception exc)
			{
				logger.error("Could not read a system property. I might be in a sandbox.");
			}
			if (guiString == null)
			{
				guiString = "stable";
			}
			try
			{
				String resource = "org.openscience.jchempaint.resources.JCPGUI_" + guiString;
				guiDefinition = ResourceBundle.getBundle(resource, Locale.getDefault());
			} catch (Exception exc)
			{
				logger.error("Could not read a GUI definition.");
				logger.debug(exc);
			}
		}
		return guiDefinition;
	}


	/**
	 *  Gets the resources attribute of the JCPPropertyHandler object
	 *
	 *@return    The resources value
	 */
	public ResourceBundle getResources()
	{
		if (resources == null)
		{
			try
			{
				String resource = "org.openscience.jchempaint.resources.JChemPaintResources";
				resources = ResourceBundle.getBundle(resource);
			} catch (Exception exc)
			{
				logger.error("Could not read the resources.");
				logger.debug(exc);
			}
		}
		return resources;
	}


	/**
	 *  Returns an URL build from the path of this object and another part that is
	 *  searched in the properties file. Used to find the images for the buttons.
	 *
	 *@param  key  String The String that says which image is searched
	 *@return      URL The URL where the image is located
	 */
	public URL getResource(String key)
	{
		String name = getResourceString(key);
		logger.debug("resource name: ", name);
		if (name != null)
		{
			URL url = this.getClass().getResource(name);
			return url;
		} else
		{
			logger.error("ResourceString is null for: ", key);
		}
		return null;
	}


	/**
	 *  Returns the ResourceString from the properties file that follows the given
	 *  String.
	 *
	 *@param  key  String The String to be looked after
	 *@return      String The String that follows the key in the properties file
	 */
	public String getResourceString(String key)
	{
		String str;
		try
		{
			str = getResources().getString(key);
		} catch (MissingResourceException mre)
		{
			logger.error("Could not find resource: ", mre.getMessage());
			logger.debug(mre);
			str = null;
		}
		return str;
	}

}

