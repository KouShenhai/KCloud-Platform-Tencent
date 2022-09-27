/**
 * Copyright (c) 2022 KCloud-Platform Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laokou.common.jvm;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import lombok.Data;
import org.laokou.common.jvm.classpath.Classpath;

import java.util.List;

/**
 * @author Kou Shenhai
 */
@Data
public class Cmd {

    @Parameter(names = {"-?","-help"}, description = "print help message", order = 3,help = true)
    private Boolean helpFlag = false;

    @Parameter(names = "-version",description = "print version and exit",order = 2)
    private Boolean versionFlag = false;

    @Parameter(names = {"-cp","-classpath"},description = "classpath",order = 1)
    private String classpath;

    @Parameter(names = "-Xjre",description = "path to jre",order = 4)
    private String jre;

    @Parameter(description = "main class and args")
    private List<String> mainClassArgs;

    private Boolean ok;

    public String getMainClass() {
        return null != mainClassArgs && !mainClassArgs.isEmpty()
                ? mainClassArgs.get(0) : null;
    }

    public List<String> getAppArgs() {
        return null != mainClassArgs && mainClassArgs.size() > 1
                ? mainClassArgs.subList(1,mainClassArgs.size()) : null;
    }

    public static Cmd parse(String[] argv) {
        Cmd args = new Cmd();
        JCommander cmd = JCommander.newBuilder().addObject(args).build();
        cmd.parse(argv);
        args.ok = true;
        return args;
    }

    public static void main(String[] args) {
        Cmd cmd = Cmd.parse(args);
        if (!cmd.ok || cmd.helpFlag) {
            System.out.print("Usage: <main class> [-options] class [args...]");
            return;
        }
        if (cmd.versionFlag) {
            System.out.println("java version 1.8.0");
            return;
        }
        startJVM(cmd);
    }

    private static void startJVM(Cmd cmd) {
        Classpath cp = new Classpath(cmd.jre,cmd.classpath);
        System.out.printf("classpath: %s\n class: %s\n args: %s \n",cp,cmd.getMainClass(),cmd.getAppArgs());
        //获取className
        final String className = cmd.getMainClass().replace(".", "/");
        try {
            final byte[] classData = cp.readClass(className);
            System.out.println("classData:");
            for (byte b : classData) {
                //16进制输出
                System.out.print(String.format("%02x",b & 0xff) + " ");
            }
        } catch (Exception e) {
            System.out.println("Could not find or load main class " + cmd.getMainClass());
            e.printStackTrace();
        }
    }

}
