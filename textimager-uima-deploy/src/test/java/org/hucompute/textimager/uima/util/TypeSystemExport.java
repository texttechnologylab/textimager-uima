package org.hucompute.textimager.uima.util;

import it.unimi.dsi.fastutil.Hash;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.apache.uima.util.TypeSystemUtil;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;


public class TypeSystemExport {

    @Test
    public void extractTypeSystem() throws UIMAException, IOException, SAXException {

        JCas c = JCasFactory.createJCas();
        TypeSystemUtil.typeSystem2TypeSystemDescription(c.getTypeSystem()).toXML(new FileOutputStream(new File("/tmp/TypeSystem.xml")));


        Set<TypeSystemDescription> descriptionSet = new HashSet<>(0);
        for (String s : TypeSystemDescriptionFactory.scanTypeDescriptors()) {
            System.out.println(s);
            if(s.startsWith("file:")) {
                TypeSystemDescription t = TypeSystemDescriptionFactory.createTypeSystemDescriptionFromPath(s);
                descriptionSet.add(t);
            }
            else{
//                TypeSystemDescription t = TypeSystemDescriptionFactory.createTypeSystemDescription(s.substring(s.lastIndexOf("/")+1));
//                descriptionSet.add(t);

            }
        }

        TypeSystemDescription merge = CasCreationUtils.mergeTypeSystems(descriptionSet);
        System.out.println(merge);

        ClassLoader loader = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader)loader).getURLs();
        for(URL url: urls){
            System.out.println(url.getFile());
        }

    }

}
