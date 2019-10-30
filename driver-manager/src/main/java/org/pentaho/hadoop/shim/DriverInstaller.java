/*******************************************************************************
 *
 * Pentaho Big Data
 *
 * Copyright (C) 2002-2019 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.hadoop.shim;

import org.apache.karaf.kar.KarService;
//import org.apache.karaf.kar.internal.Kar;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DriverInstaller {

    private KarService karService;
    private static final String karSourceDir = "/Users/aramos/Documents/TEMP/TRASH/KarTest/";

    public void setKarService(KarService karService) {
        this.karService = karService;
        try {
            List<String> karsInDir = Files.list( new File(karSourceDir).toPath())
                    .filter(path -> path.toString().endsWith(".kar"))
                    .map(path -> path.getFileName().toString().substring(0,path.getFileName().toString().length()-4))
                    .collect(Collectors.toList());
            List<String> karsToInstall = new ArrayList(karsInDir);
            karsToInstall.removeAll(karService.list());
            List<String> karsToRemove = new ArrayList(karService.list());
            karsToRemove.removeAll(karsInDir);

            //karService.install(new File(karSourceDir + "pentaho-hadoop-shims-cdh61-kar-9.0.0.0-SNAPSHOT.kar").toURI());

            karsToRemove.stream()
                    .forEach( name -> {
                        try {
                            karService.uninstall(name);
                            System.out.println ( "################################################" );
                            System.out.println( name + " Kar removed" );
                            System.out.println( "################################################" );
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

            karsToInstall.stream()
                    .forEach( name -> {
                        try {
                            karService.install(new File(karSourceDir + name + ".kar").toURI());
                            System.out.println ( "################################################" );
                            System.out.println( name + " Kar installed" );
                            System.out.println( "################################################" );
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println ( "################################################" );
        System.out.println( "KarService loaded" );
        System.out.println( "################################################" );
    }
}
