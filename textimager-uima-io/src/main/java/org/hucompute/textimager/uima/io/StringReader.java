package org.hucompute.textimager.uima.io;

import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ResourceMetaData;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;
import org.dkpro.core.api.parameter.ComponentParameters;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;


@ResourceMetaData(name = "String Reader")
@TypeCapability(
        outputs = {
            "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData"})
public class StringReader
    extends JCasCollectionReader_ImplBase
{
    /**
     * Set this as the language of the produced documents.
     */
    public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
    private String language;

    /**
     * The document text.
     */
    public static final String PARAM_DOCUMENT_TEXT = "documentTexts";
    @ConfigurationParameter(name = PARAM_DOCUMENT_TEXT, mandatory = true)
    private String[] documentText;

    /**
     * The collection ID to set in the {@link DocumentMetaData}.
     */
    public static final String PARAM_COLLECTION_ID = "collectionId";
    @ConfigurationParameter(name = PARAM_COLLECTION_ID, mandatory = true,
            defaultValue = "COLLECTION_ID")
    private String collectionId;


    /**
     * The document base URI to set in the {@link DocumentMetaData}.
     */
    public static final String PARAM_DOCUMENT_BASE_URI = "documentBaseUri";
    @ConfigurationParameter(name = PARAM_DOCUMENT_BASE_URI, mandatory = false)
    private String documentBaseUri;

    
    private int index = 0;

    @Override
    public void initialize(UimaContext aContext)
        throws ResourceInitializationException
    {
        super.initialize(aContext);
    }

    @Override
    public void getNext(JCas sJCas)
        throws IOException
    {

        DocumentMetaData meta = DocumentMetaData.create(sJCas);
        meta.setCollectionId(collectionId);
        meta.setDocumentUri("Document"+index);
        meta.setDocumentId("Document"+index);
        meta.setDocumentBaseUri(documentBaseUri);

        sJCas.setDocumentLanguage(language);
        sJCas.setDocumentText(documentText[index]);
        index++;
    }

    @Override
    public boolean hasNext()
        throws IOException, CollectionException
    {
        return index<documentText.length;
    }

    @Override
    public Progress[] getProgress()
    {
        return new Progress[] { new ProgressImpl(index , documentText.length, Progress.ENTITIES) };
    }
}