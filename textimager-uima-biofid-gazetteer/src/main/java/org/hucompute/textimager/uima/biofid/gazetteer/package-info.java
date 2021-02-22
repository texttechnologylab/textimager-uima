/**
 * org.biofid.gazetter contains a skip-gram taxon and entity tagger for UIMA JCas.<br>
 * It creates a set of unique skip-grams for each entry in a list of taxa with their URIs extracted from ontologies.<br>
 * Skip-gram matches in the JCas are then mapped onto the original URis.<br>
 * It was created by the Text Technology Lab, Goethe University Frankfurt for BIOfid project
 * and is part of the following publication:
 * <ul><li>
 * S. Ahmed, M. Stoeckel, C. Driller, A. Pachzelt, and A. Mehler,<br>
 * "BIOfid Dataset: Publishing a German Gold Standard for Named Entity Recognition in Historical Biodiversity Literature,"
 * in Proceedings of KONVENS 2019, 2019.<br>
 * <i>accepted</i>
 * </li></ul>
 * Author: Manuel Stoeckel<br>
 * Contact: manuel.stoeckel (at) stud.uni-frankfurt.de<br>
 * Licence: AGPL-3.0<br>
 *
 * @see <a href="https://www.biofid.de/">BIOfid</a>
 * @see <a href="https://www.texttechnologylab.org/">Text Technology Lab</a>
 */
package org.hucompute.textimager.uima.biofid.gazetteer;