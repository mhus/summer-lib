/**
 * Copyright (C) 2002 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.core.util;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class SimpleFileProvider implements FileResolver {

    private File root;

    public SimpleFileProvider(File root) {
        this.root = root;
    }

    @Override
    public File getFile(String path) {
        return new File(root, path);
    }

    @Override
    public Set<String> getContent(String path) {
        File file = getFile(path);
        HashSet<String> out = new HashSet<>();
        for (File sub : file.listFiles()) {
            try {
                if (sub.isDirectory()) {
                    out.add(path + sub.getName() + "/");
                } else {
                    out.add(path + sub.getName());
                }
            } catch (Throwable t) {
            }
        }
        return out;
    }

    @Override
    public File getRoot() {
        return root;
    }
}
