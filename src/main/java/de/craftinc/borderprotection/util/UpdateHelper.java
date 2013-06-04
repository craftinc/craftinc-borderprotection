/*  Craft Inc. BorderProtection
    Copyright (C) 2013  Paul Schulze

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package de.craftinc.borderprotection.util;

import de.craftinc.borderprotection.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UpdateHelper
{
    /**
     * The URL from which the Plugin tries to get the latest version.
     */
    @SuppressWarnings("FieldCanBeLocal")
    private static final String updateUrl = "http://www.craftinc.de/plugins/update/craftinc-borderprotection";

    /**
     * The latest version which was seen on last check.
     */
    public static String cachedLatestVersion = null;

    /**
     * Gets the latest version from the updateURL and returns it as <code>String</code>.
     *
     * @return latest version as <code>String</code>.
     */
    @SuppressWarnings("StringBufferMayBeStringBuilder")
    public static String getLatestVersion()
    {
        // StringBuffer is thread-safe. Don't know if this is really important here, but safe is safe :).
        StringBuffer s = new StringBuffer();
        try
        {
            URLConnection c = new URL(updateUrl).openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
            String inputLine;
            while ( ( inputLine = br.readLine() ) != null )
            {
                s.append(inputLine);
            }
            br.close();
        }
        catch ( MalformedURLException e )
        {
            Plugin.instance.getLogger().warning("Could not check for latest version. Update URL is malformed.");
        }
        catch ( IOException e )
        {
            Plugin.instance.getLogger().warning("Could not check for latest version. Update URL was not readable.");
        }

        // update cached latest version
        cachedLatestVersion = s.toString();

        return s.toString();
    }

    /**
     * Gets the current version of this plugin directly from the plugin.yml version entry.
     *
     * @return current version as <code>String</code>.
     */
    public static String getCurrentVersion()
    {
        return Plugin.instance.getDescription().getVersion();
    }

    /**
     * Checks if a newer version is available.
     *
     * @return Boolean
     */
    public static Boolean newVersionAvailable()
    {
        final String version = getLatestVersion();

        // do not show beta or dev versions
        if (version.contains("beta") || version.contains("dev"))
            return false;

        return !getCurrentVersion().equals(getLatestVersion());
    }
}
