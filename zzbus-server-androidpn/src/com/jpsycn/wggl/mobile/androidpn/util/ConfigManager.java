/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.jpsycn.wggl.mobile.androidpn.util;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationFactory;
import org.apache.log4j.Logger;

/** 
 * This class is to manage the applicatin configruation.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class ConfigManager {

    private final static Logger log = Logger.getLogger(ConfigManager.class);
    private static Configuration config;

    private static ConfigManager instance;

    private ConfigManager() {
        loadConfig();
    }

    /**
     * Returns the singleton instance of ConfigManger.
     * 
     * @return the instance
     */
    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                instance = new ConfigManager();
            }
        }
        return instance;
    }

    /**
     * Loads the default configuration file.
     */
    public void loadConfig() {
        loadConfig("config.xml");
    }

    /**
     * Loads the specific configuration file.
     * 
     * @param configFileName the file name
     */
    public void loadConfig(String configFileName) {
        try {
            ConfigurationFactory factory = new ConfigurationFactory(
                    configFileName);
            config = factory.getConfiguration();
            log.info("Configuration loaded: " + configFileName);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException("Configuration loading error: "
                    + configFileName, ex);
        }
    }

    /**
     * Returns the loaded configuration object.
     * 
     * @return the configuration
     */
    public Configuration getConfig() {
        return config;
    }

}
