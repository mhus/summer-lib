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
package de.mhus.lib.core;

import java.io.InputStream;
import java.net.URL;

import de.mhus.lib.annotations.activator.DefaultFactory;
import de.mhus.lib.annotations.activator.DefaultImplementation;
import de.mhus.lib.annotations.activator.DefaultImplementationNull;
import de.mhus.lib.annotations.activator.ObjectFactory;
import de.mhus.lib.annotations.lang.Prototype;
import de.mhus.lib.core.activator.DefaultActivator;
import de.mhus.lib.core.util.Injector;
import de.mhus.lib.core.util.InjectorList;

@DefaultImplementation(DefaultActivator.class)
public abstract class MActivator extends ClassLoader {

    //	protected static StaticBase base = new StaticBase();

    protected InjectorList injector;

    private boolean destroyed = false;

    public MActivator() {
        //		this(Thread.currentThread().getContextClassLoader());
        this(MActivator.class.getClassLoader());
    }

    public MActivator(ClassLoader loader) {
        super(loader);
    }

    public <T> T createObject(Class<T> ifc, String name) throws Exception {
        return (T) createObject(ifc, name, null, null);
    }

    public Object createObject(String name) throws Exception {
        return createObject(name, null, null);
    }

    protected abstract Object mapName(String name);

    @SuppressWarnings("unchecked")
    public <T> T createObject(Class<T> ifc, String name, Class<?>[] classes, Object[] objects)
            throws Exception {
        return (T) createObject(ifc.getCanonicalName() + ":" + name, classes, objects);
    }

    public Object createObject(String name, Class<?>[] classes, Object[] objects) throws Exception {

        Object obj = mapName(name);

        Class<?> clazz = null;
        if (obj instanceof String) {
            String s = (String) obj;
            int p = s.indexOf(":");
            if (p >= 0) {
                s = s.substring(p + 1);
                if (s.length() == 0) s = (String) obj; // reset
            }
            clazz = loadClass(s);
        } else if (obj instanceof Class) {
            clazz = (Class<?>) obj;
        } else clazz = obj.getClass();

        DefaultImplementation defaultImplementation =
                clazz.getAnnotation(DefaultImplementation.class);
        if (defaultImplementation != null) {
            clazz = defaultImplementation.value();
        }

        Object out = null;

        DefaultFactory factoryClazz = clazz.getAnnotation(DefaultFactory.class);
        if (factoryClazz != null) {
            ObjectFactory inst = getObject(factoryClazz.value());
            if (inst != null) {
                out = inst.create(clazz, classes, objects);
            }
        }
        if (out == null) {
            if (classes == null || objects == null)
                out = clazz.getDeclaredConstructor().newInstance();
            else out = clazz.getConstructor(classes).newInstance(objects);
        }
        if (injector != null) injector.doInject(out);

        return out;
    }

    public InputStream getResourceStream(String name) throws Exception {
        Object obj = mapName(name);

        if (obj instanceof String) return getResourceAsStream((String) obj);
        if (obj instanceof InputStream) return (InputStream) obj;
        if (obj instanceof Class)
            return (InputStream) ((Class<?>) obj).getDeclaredConstructor().newInstance();
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(Class<T> ifc, String name) throws Exception {
        return (T) getObject(ifc.getCanonicalName() + ":" + name);
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(Class<T> ifc) throws Exception {
        return (T) getObject(ifc.getCanonicalName());
    }

    public Object getObject(String name) throws Exception {

        if (isInstance(name)) return getInstance(name);

        Class<?> clazz = findClass(name);
        Class<?> orgClazz = clazz;

        DefaultImplementationNull defaultImplementationNull =
                clazz.getAnnotation(DefaultImplementationNull.class);
        if (defaultImplementationNull != null) {
            return null;
        }

        DefaultImplementation defaultImplementation =
                clazz.getAnnotation(DefaultImplementation.class);
        if (defaultImplementation != null) {
            clazz = defaultImplementation.value();
            if (clazz == null) return null;
        }
        DefaultFactory defaultFactory = clazz.getAnnotation(DefaultFactory.class);
        if (defaultFactory != null) {
            ObjectFactory factory = getObject(defaultFactory.value());
            return factory.create(orgClazz, null, null);
        }

        if (clazz.isInterface()) return null;
        Object obj = clazz.getDeclaredConstructor().newInstance();
        if (injector != null) injector.doInject(obj);

        if (clazz.getAnnotation(Prototype.class) == null
                && orgClazz.getAnnotation(Prototype.class) == null) setInstance(name, obj);

        return obj;
    }

    protected abstract Object getInstance(String name);

    protected abstract void setInstance(String name, Object obj);

    public Class<?> getClazz(String name) throws Exception {
        Object obj = mapName(name);
        if (obj instanceof String) return loadClass((String) obj);
        if (obj instanceof Class) return (Class<?>) obj;
        return obj.getClass();
    }

    public URL getURL(String path) {
        return getResource(path);
    }

    public InjectorList getInjector() {
        return injector;
    }

    public void setInjector(InjectorList injector) {
        this.injector = injector;
    }

    public void addInjector(Injector injector) {
        if (this.injector == null) this.injector = new InjectorList();
        this.injector.add(injector);
    }

    public void destroy() {
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public boolean isInstance(Class<?> ifc) {
        return isInstance(ifc.getCanonicalName());
    }

    public abstract boolean isInstance(String ifc);

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        String n = name;
        if (n.indexOf(':') > 0) n = n.substring(0, n.indexOf(':'));

        Object obj = mapName(n);
        if (obj instanceof String) {
            return getParent().loadClass((String) obj);
        } else if (obj instanceof Class) {
            return (Class<?>) obj;
        }
        return getParent().loadClass(name);
    }

    @Override
    protected URL findResource(String name) {
        Object obj = mapName(name);
        if (obj instanceof URL) return (URL) obj;
        else if (obj instanceof String) return getParent().getResource((String) obj);

        return getParent().getResource(name);
    }
}
