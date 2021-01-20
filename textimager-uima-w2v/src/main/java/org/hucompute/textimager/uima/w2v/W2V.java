package org.hucompute.textimager.uima.w2v;

import com.srbenoit.math.delaunay.GraphEdge;
import com.srbenoit.math.delaunay.Vertex;
import com.srbenoit.math.delaunay.Voronoi;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unihd.dbs.uima.types.heideltime.Timex3;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.DoubleArray;
import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.learning.impl.elements.CBOW;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.plot.Tsne;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.hucompute.textimager.uima.type.ImageVector;
import org.hucompute.textimager.uima.type.Sentiment;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.*;

//import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
//import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
//import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
//import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
//import org.hucompute.services.type.DocElement;
//import org.hucompute.services.type.GrammaticalCategory;
//import org.hucompute.services.type.ImageVector;
//import org.hucompute.services.type.Sentiment;

//import util.Util;
public class W2V extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String docString = "";

		double[][] mainMatrix = null;
		RealMatrix mainRealMatrix = null;
		WeightLookupTable<VocabWord> mainLookupTable = null;
		System.out.println("Bereite W2V Daten vor");

			docString = "";
			for(Sentence tmpSen : JCasUtil.select(aJCas, Sentence.class)){
				HashSet<Token> alreadySet = new HashSet<Token>();
				for(Timex3 tmpTime : JCasUtil.selectCovered(aJCas, Timex3.class, tmpSen)){
					for(Token tmpToken : JCasUtil.selectCovered(aJCas, Token.class, tmpTime)){
						alreadySet.add(tmpToken);
					}
				}

				for(Token tmpToken : JCasUtil.selectCovered(aJCas, Token.class, tmpSen)){
					String tmpPos = tmpToken.getPos().getPosValue();
					List<Sentiment> tmpSentiment = JCasUtil.selectCovered(aJCas, Sentiment.class, tmpToken);
					String tmpSentValue = "";
					if(!tmpSentiment.isEmpty()){
						if(tmpSentiment.get(0).getSentiment()>0)
							tmpSentValue = "positive_";
						else
							if(tmpSentiment.get(0).getSentiment()<0)
								tmpSentValue = "negative_";
					}
					if(alreadySet.contains(tmpToken)){
						docString += tmpSentValue+"time ";
					}
					else{
						if(tmpPos.equals("ADV")){
							if(tmpPos.startsWith("ADV"))
								docString += tmpSentValue+"ADV ";
						}
						else{
							if(!JCasUtil.selectCovered(aJCas, MorphologicalFeatures.class, tmpToken).isEmpty()){
								for(MorphologicalFeatures tmpGram : JCasUtil.selectCovered(aJCas, MorphologicalFeatures.class, tmpToken)){
									if(tmpPos.startsWith("V") || tmpPos.startsWith("ADJ") || tmpPos.equals("ADV") || tmpPos.startsWith("N") || tmpPos.startsWith("PI") || tmpPos.startsWith("PP") || tmpPos.startsWith("PR") || tmpPos.startsWith("PD") || tmpPos.startsWith("PW")){
										String tmpString = "";
										tmpString += tmpGram.getValue() + tmpGram.getCase() + "_" + tmpGram.getDegree() + "_" + tmpGram.getGender() + "_" + tmpGram.getMood() + "_" + tmpGram.getNumber() + "_" + tmpGram.getPerson() + "_" + tmpGram.getTense() + "_" + tmpGram.getVoice() + " ";
										tmpString = tmpString.replaceAll("null_", "");
										tmpString = tmpString.replaceAll("_null", "");
										tmpString = tmpString.replaceAll("null", "");
										//										tmpString = tmpString.replaceAll(" ", "");
										if(!tmpString.equals(" ")){
											// Nur finite Verben mit den Grammatics
											if(tmpPos.startsWith("V"))
												docString += tmpSentValue+"V_" + tmpString;
											if(tmpPos.startsWith("N"))
												docString += tmpSentValue+"N_" + tmpString;
											if(tmpPos.startsWith("ADJ"))
												docString += tmpSentValue+"ADJ_" + tmpString;
											if(tmpPos.startsWith("PI") || tmpPos.startsWith("PP") || tmpPos.startsWith("PR") || tmpPos.startsWith("PD") || tmpPos.startsWith("PW"))
												docString += tmpSentValue+"P_" + tmpString;
										}
									}
								}
							}
							else{
								// Packt auch die Finiten Verben etc. mit den Sentiments dazu
								if(!tmpSentValue.equals("")){
									docString += tmpSentValue+tmpPos+" ";
								}
							}
						}
					}
				}
				if(!docString.isEmpty()){
					docString = docString.substring(0, docString.length()-1);
					docString += "\n";
				}
			}
			//			System.out.println(docString);

			String[] sentences = docString.split("\n");
			List<String> sentenceCollection = new ArrayList<String>();
			//			System.out.println(docString);
			for(String tmpSen : sentences){
				sentenceCollection.add(tmpSen);
			}
			System.out.println("Berechne Word Embeddings");
			System.out.println(sentenceCollection);
			SentenceIterator iter = new CollectionSentenceIterator(sentenceCollection);
			TokenizerFactory t = new DefaultTokenizerFactory();

			Word2Vec vec = new Word2Vec.Builder()
        	.elementsLearningAlgorithm(new CBOW<VocabWord>())
//			        	.elementsLearningAlgorithm(new SkipGram<VocabWord>())
			.minWordFrequency(1)
			.iterations(100)
			.learningRate(0.25)
			.layerSize(50)
			.seed(123)
			.windowSize(5)
			.iterate(iter)
			.tokenizerFactory(t)
			.build();

			vec.fit();
			Iterator<VocabWord> vw = vec.getLookupTable().getVocabCache().vocabWords().iterator();
			//			while(vw.hasNext()){
			//				System.out.print(vw.next().getWord() + ", ");
			//			}
			//			System.out.println();
			//			System.out.println(vec.getLookupTable().getVocabCache().vocabWords().size());
			Tsne tsne = new Tsne.Builder()
			.setMaxIter(10)
			.normalize(true)
			.learningRate(500)
			.useAdaGrad(true)
			.usePca(false)
			.perplexity(30)
			.build();

			InMemoryLookupTable<?> table = (InMemoryLookupTable<?>) vec.lookupTable();
			INDArray tsn = tsne.calculate(table.getSyn0(), 2, 30);
			//			INDArray tsn = table.getSyn0();


			// Mit Procrustes Begin
			if(mainLookupTable != null){
				System.out.println("Existiert schon");
				// Berechne gemeinsame Woerter
				int sameCount = 0;
				for(VocabWord vb : mainLookupTable.getVocabCache().vocabWords()){
					if(vec.getLookupTable().getVocabCache().vocabWords().contains(vb))
						sameCount++;
				}

				double[][] tmpMatrix = new double[sameCount][tsn.columns()];
				double[][] tmpMainMatrix = new double[sameCount][tsn.columns()];

				int i = 0;
				for(VocabWord vb : mainLookupTable.getVocabCache().vocabWords()){
					if(vec.getLookupTable().getVocabCache().vocabWords().contains(vb)){
						int indexTmp = vec.getLookupTable().getVocabCache().indexOf(vb.getWord());
						int indexMain = mainLookupTable.getVocabCache().indexOf(vb.getWord());
						for(int k=0; k<tsn.columns(); k++){
							tmpMatrix[i][k] = tsn.getRow(indexTmp).getDouble(k);
							tmpMainMatrix[i][k] = mainMatrix[indexMain][k];
						}
						i++;
					}
				}
				RealMatrix tmpRealMatrix = MatrixUtils.createRealMatrix(tmpMatrix);
				RealMatrix tmpMainRealMatrix = MatrixUtils.createRealMatrix(tmpMainMatrix);
				Procrustes proc = new Procrustes(tmpRealMatrix, tmpMainRealMatrix, true, true);
				//				RealMatrix procustinatedRealMatrix = proc.procrustinate(tmpRealMatrix);
				double[][] tmpAllMatrix = new double[tsn.rows()][tsn.columns()];
				for(VocabWord vb :vec.getLookupTable().getVocabCache().vocabWords()){
					int tmpIndex = vec.getLookupTable().getVocabCache().indexOf(vb.getWord());
					for(int k=0; k<tsn.getRow(tmpIndex).columns(); k++){
						tmpAllMatrix[tmpIndex][k] = tsn.getRow(tmpIndex).getDouble(k);
					}
				}
				RealMatrix tmpAllRealMatrix = MatrixUtils.createRealMatrix(tmpAllMatrix);
				//							System.out.println("Vor Transformierung: ");
				//							System.out.println(tmpAllRealMatrix.getEntry(indexOfXY, 0) + ", " + tmpAllRealMatrix.getEntry(indexOfXY, 1));
				//							System.out.println("R: " + proc.getR());
				//Procustination
				RealMatrix tt = new Array2DRowRealMatrix(tsn.rows(), tsn.columns());
				for (int k = 0; k < tsn.rows(); k++) {
					tt.setRowMatrix(k, proc.getTranslation().transpose());
				}

				// rotate, scale and translate
				RealMatrix allProcrustinatedRealMatrix = tmpAllRealMatrix.multiply(proc.getR()).scalarMultiply(proc.getDilation()).add(tt);  // Was a bug here
				//				RealMatrix allProcrustinatedRealMatrix = proc.procrustinate(tmpAllRealMatrix);
				//							System.out.println("Nach Transformierung: ");
				//							System.out.println(allProcrustinatedRealMatrix.getEntry(indexOfXY, 0) + ", " + allProcrustinatedRealMatrix.getEntry(indexOfXY, 1));
				for(int k = 0; k<allProcrustinatedRealMatrix.getRowDimension(); k++){
					org.hucompute.textimager.uima.type.Word2Vec embedding = new org.hucompute.textimager.uima.type.Word2Vec(aJCas, 0, aJCas.getDocumentText().length());
					DoubleArray da = new DoubleArray(aJCas, allProcrustinatedRealMatrix.getColumnDimension());
					for(int l = 0; l<allProcrustinatedRealMatrix.getColumnDimension(); l++){
						da.set(l, allProcrustinatedRealMatrix.getEntry(k, l));
					}
					//					if(vec.getLookupTable().getVocabCache().wordAtIndex(k).equals("XY")){
					//						System.out.println("schreibe:");
					//						if(k == indexOfXY)
					//							System.out.println("index gleich");
					//						else
					//							System.out.println("index nicht gleich");
					//						System.out.println(da);
					//					}
					embedding.setEmbedding(da);
					embedding.setValue(vec.getLookupTable().getVocabCache().wordAtIndex(k));
					embedding.addToIndexes(aJCas);
				}
			}
			else{
				System.out.println("Existiert noch nicht");
				mainLookupTable = vec.getLookupTable();
				mainMatrix = new double[tsn.rows()][tsn.columns()];
				System.out.println("WÃ¶rter: " + vec.getLookupTable().getVocabCache().vocabWords().size()) ;
				System.out.println("WÃ¶rter TSNE: " + tsn.rows()) ;
				System.out.println("Spalte TSNE: " + tsn.columns()) ;
				for(VocabWord vb :vec.getLookupTable().getVocabCache().vocabWords()){
					org.hucompute.textimager.uima.type.Word2Vec embedding = new org.hucompute.textimager.uima.type.Word2Vec(aJCas, 0, aJCas.getDocumentText().length());
					DoubleArray da = new DoubleArray(aJCas, tsn.columns());
					for(int k=0; k<tsn.columns(); k++){
						int indexOfTmpWord = vec.getLookupTable().getVocabCache().indexOf(vb.getWord());
						mainMatrix[indexOfTmpWord][k] = tsn.getDouble(indexOfTmpWord, k);
						da.set(k, tsn.getDouble(indexOfTmpWord, k));
					}
					//					if(vb.getWord().equals("XY")){
					//						System.out.println("schreibe:");
					//						if(vec.getLookupTable().getVocabCache().indexOf(vb.getWord()) == indexOfXY)
					//							System.out.println("index gleich");
					//						else
					//							System.out.println("index nicht gleich");
					//						System.out.println(vb.getWord());
					//						System.out.println(da);
					//					}
					embedding.setEmbedding(da);
					embedding.setValue(vb.getWord());
					embedding.addToIndexes(aJCas);
				}
				mainRealMatrix = MatrixUtils.createRealMatrix(mainMatrix);
			}
			//Mit Procrustes Ende

			// Ohne Procrustes
			//			for(VocabWord vb :vec.getLookupTable().getVocabCache().vocabWords()){
			//				org.hucompute.services.type.Word2Vec embedding = new org.hucompute.services.type.Word2Vec(aJCas, tmpDoc.getBegin(), tmpDoc.getEnd());
			//				DoubleArray da = new DoubleArray(aJCas, tsn.columns());
			//				int tmpIndex = vec.getLookupTable().getVocabCache().indexOf(vb.getWord());
			//				for(int k=0; k<tsn.getRow(tmpIndex).columns(); k++){
			//					//					mainMatrix[tmpIndex][k] = tsn.getRow(tmpIndex).getDouble(k);
			//					da.set(k, tsn.getRow(tmpIndex).getDouble(k));
			//				}
			//				embedding.setEmbedding(da);
			//				embedding.setValue(vb.getWord());
			//				embedding.addToIndexes(aJCas);
			//			}


		System.out.println("Embeddings fertig");
		System.out.println("Berechne Document Vektoren");


		HashMap<String, Vertex> allFeatures = new HashMap<String, Vertex>();
		for(org.hucompute.textimager.uima.type.Word2Vec tmpW2V : JCasUtil.select(aJCas, org.hucompute.textimager.uima.type.Word2Vec.class)){
			allFeatures.put(tmpW2V.getValue(), new Vertex(tmpW2V.getEmbedding(0), tmpW2V.getEmbedding(1)));
		}

			HashMap<String, ArrayList<GraphEdge>> edgeVerteilung = new HashMap<String, ArrayList<GraphEdge>>();
			//				HashMap<String, ArrayList<GraphEdge>> nodeOrder = new HashMap<String, ArrayList<GraphEdge>>();
			HashMap<String, Integer> featureCount = new HashMap<String, Integer>();
			HashMap<String, Double> frequencyValue = new HashMap<String, Double>();
			HashMap<String, HashSet<Sentence>> featureVerteilung = new HashMap<String, HashSet<Sentence>>();
			HashMap<String, Double> isf = new HashMap<String, Double>();
			HashMap<String, Double> closenessCentrality = new HashMap<String, Double>();
			HashMap<String, Integer> edgeCount = new HashMap<String, Integer>();
			HashMap<String, ArrayList<Double>> form = new HashMap<String, ArrayList<Double>>();
			HashMap<String, ArrayList<Vertex>> nodeOrder = new HashMap<String, ArrayList<Vertex>>();
			HashMap<String, Double> flaeche = new HashMap<String, Double>();
			// Hier muss der Vektor erstellt werden.
			for(Sentence tmpSen : JCasUtil.select(aJCas, Sentence.class)){
				HashSet<Token> alreadySet = new HashSet<Token>();
				for(Timex3 tmpTime : JCasUtil.selectCovered(aJCas, Timex3.class, tmpSen)){
					for(Token tmpToken : JCasUtil.selectCovered(aJCas, Token.class, tmpTime)){
						alreadySet.add(tmpToken);
					}
				}
				for(Token tmpToken : JCasUtil.selectCovered(aJCas, Token.class, tmpSen)){
					String tmpTokenString = tmpToken.getCoveredText();
					String tmpPos = tmpToken.getPos().getPosValue();
					Boolean withSentiment = false;
					List<Sentiment> tmpSentiment = JCasUtil.selectCovered(aJCas, Sentiment.class, tmpToken);
					String tmpSentValue = "";
					if(!tmpSentiment.isEmpty()){
						withSentiment = true;
						if(tmpSentiment.get(0).getSentiment()>0){
							tmpSentValue = "positive_";
							//								if(featureVerteilung.containsKey(tmpSentValue)){
							//									featureVerteilung.get(tmpSentValue).add(tmpSen);
							//								}
							//								else{
							//									featureVerteilung.put(tmpSentValue, new HashSet<Sentence>(Arrays.asList(tmpSen)));
							//								}
							//								// Erstellt den FeatureCount
							//								if(featureCount.containsKey(tmpSentValue))
							//									featureCount.put(tmpSentValue, (featureCount.get(tmpSentValue)+1));
							//								else
							//									featureCount.put(tmpSentValue, 1);
						}
						else
							if(tmpSentiment.get(0).getSentiment()<0){
								tmpSentValue = "negative_";
								//									if(featureVerteilung.containsKey(tmpSentValue)){
								//										featureVerteilung.get(tmpSentValue).add(tmpSen);
								//									}
								//									else{
								//										featureVerteilung.put(tmpSentValue, new HashSet<Sentence>(Arrays.asList(tmpSen)));
								//									}
								//									// Erstellt den FeatureCount
								//									if(featureCount.containsKey(tmpSentValue))
								//										featureCount.put(tmpSentValue, (featureCount.get(tmpSentValue)+1));
								//									else
								//										featureCount.put(tmpSentValue, 1);
							}
					}

					//						tmpSentValue = ""; // Das bei MITUNDERSCORE RAUSNEHMEN!

					if(alreadySet.contains(tmpToken)){
						String tmpKey = tmpSentValue + "time";
						// Erstellt die FeatureVerteilung
						if(featureVerteilung.containsKey(tmpKey)){
							featureVerteilung.get(tmpKey).add(tmpSen);
						}
						else{
							featureVerteilung.put(tmpKey, new HashSet<Sentence>(Arrays.asList(tmpSen)));
						}
						// Erstellt den FeatureCount
						if(featureCount.containsKey(tmpKey))
							featureCount.put(tmpKey, (featureCount.get(tmpKey)+1));
						else
							featureCount.put(tmpKey, 1);
					}
					else{
						// Erstellt die FeatureVerteilung
						if(tmpPos.equals("ADV")){
							String tmpKey = tmpSentValue + "ADV";
							if(featureVerteilung.containsKey(tmpKey)){
								featureVerteilung.get(tmpKey).add(tmpSen);
							}
							else{
								featureVerteilung.put(tmpKey, new HashSet<Sentence>(Arrays.asList(tmpSen)));
							}
							// Erstellt den FeatureCount
							if(featureCount.containsKey(tmpKey))
								featureCount.put(tmpKey, (featureCount.get(tmpKey)+1));
							else
								featureCount.put(tmpKey, 1);
						}
						else{
							if(!JCasUtil.selectCovered(aJCas, MorphologicalFeatures.class, tmpToken).isEmpty()){
								for(MorphologicalFeatures tmpGram : JCasUtil.selectCovered(aJCas, MorphologicalFeatures.class, tmpToken)){
									if(tmpPos.startsWith("V") || tmpPos.startsWith("ADJ") || tmpPos.equals("ADV") || tmpPos.startsWith("N") || tmpPos.startsWith("PI") || tmpPos.startsWith("PP") || tmpPos.startsWith("PR") || tmpPos.startsWith("PD") || tmpPos.startsWith("PW")){
										String tmpString = tmpGram.getValue() + tmpGram.getCase() + "_" + tmpGram.getDegree() + "_" + tmpGram.getGender() + "_" + tmpGram.getMood() + "_" + tmpGram.getNumber() + "_" + tmpGram.getPerson() + "_" + tmpGram.getTense() + "_" + tmpGram.getVoice() + " ";
										tmpString = tmpString.replaceAll("null_", "");
										tmpString = tmpString.replaceAll("_null", "");
										tmpString = tmpString.replaceAll("null", "");
										tmpString = tmpString.replace(" ", "");
										if(!tmpString.equals("")){
											String tmpKey = "";
											if(tmpPos.startsWith("V"))
												tmpKey = tmpSentValue + "V_" + tmpString;

											if(tmpPos.startsWith("N"))
												tmpKey = tmpSentValue + "N_" + tmpString;

											if(tmpPos.startsWith("ADJ"))
												tmpKey = tmpSentValue + "ADJ_" + tmpString;

											if(tmpPos.startsWith("PI") || tmpPos.startsWith("PP") || tmpPos.startsWith("PR") || tmpPos.startsWith("PD") || tmpPos.startsWith("PW"))
												tmpKey = tmpSentValue + "P_" + tmpString;

											// Erstellt die FeatureVerteilung
											if(featureVerteilung.containsKey(tmpKey)){
												featureVerteilung.get(tmpKey).add(tmpSen);
											}
											else{
												featureVerteilung.put(tmpKey, new HashSet<Sentence>(Arrays.asList(tmpSen)));
											}
											// Erstellt den FeatureCount
											if(featureCount.containsKey(tmpKey))
												featureCount.put(tmpKey, (featureCount.get(tmpKey)+1));
											else
												featureCount.put(tmpKey, 1);
										}
									}
								}
							}
							else{
								//									 Packt auch die Finiten Verben mit den Sentiments dazu
								if(withSentiment){
									String tmpKey = tmpSentValue + "" + tmpPos;
									// Erstellt die FeatureVerteilung
									if(featureVerteilung.containsKey(tmpKey)){
										featureVerteilung.get(tmpKey).add(tmpSen);
									}
									else{
										featureVerteilung.put(tmpKey, new HashSet<Sentence>(Arrays.asList(tmpSen)));
									}
									// Erstellt den FeatureCount
									if(featureCount.containsKey(tmpKey))
										featureCount.put(tmpKey, (featureCount.get(tmpKey)+1));
									else
										featureCount.put(tmpKey, 1);
								}
							}
						}

					}
				}
			}
			int maxCount = 0;
			for(String key : featureCount.keySet()){
				if(featureCount.get(key)>maxCount)
					maxCount = featureCount.get(key);
			}
			for(String key : featureCount.keySet()){
				frequencyValue.put(key, ((double)featureCount.get(key)/maxCount));
			}
			// Berechnet ISF
			int sentencesInDoc = JCasUtil.select(aJCas, Sentence.class).size();
			for (String key : featureVerteilung.keySet()) {
				isf.put(key, ((double) featureVerteilung.get(key).size()/sentencesInDoc));
			}
			int count = 0;
			//				File tmpW2VFile = new File(w2v.getAbsoluteFile() + "/" + tmpDoc.getName().split("_")[0] + "W2V.txt-result");
			HashMap<String, Vertex> coords = new HashMap<String, Vertex>();
			for(org.hucompute.textimager.uima.type.Word2Vec tmpW2V : JCasUtil.select(aJCas, org.hucompute.textimager.uima.type.Word2Vec.class)){
				//				System.out.println(tmpW2V.getValue() + " - " + tmpW2V.getEmbedding());
				coords.put(tmpW2V.getValue(), new Vertex(tmpW2V.getEmbedding(0), tmpW2V.getEmbedding(1)));
			}
			//			for(org.hucompute.services.type.Word2Vec tmpW2V : JCasUtil.select(aJCas, org.hucompute.services.type.Word2Vec.class)){
			//			}
			Vertex[] vertices = new Vertex[coords.keySet().size()];
			ArrayList<String> sortedFeatures = new ArrayList<String>();
			double maxDistance = Math.sqrt((100*100) + (100*100));
			double minX = Double.MAX_VALUE;
			double maxX = Double.MIN_VALUE;
			double minY = Double.MAX_VALUE;
			double maxY = Double.MIN_VALUE;
			for(String key : coords.keySet()) {
				Vertex tmp = coords.get(key);
				if(tmp.xPos > maxX)
					maxX = tmp.xPos;
				if(tmp.yPos > maxY)
					maxY = tmp.yPos;
				if(tmp.xPos < minX)
					minX = tmp.xPos;
				if(tmp.yPos < minY)
					minY = tmp.yPos;
			}
			for(String key : coords.keySet()) {
				if(!key.equals("</s>")){
					Vertex tmp = coords.get(key);
					Vertex newVertex = new Vertex((100 * ((tmp.xPos - minX) / (maxX - minX))), (100 * ((tmp.yPos - minY) / (maxY - minY))));
					vertices[count++] = newVertex;
					sortedFeatures.add(key);
					coords.put(key, newVertex);
				}
				else{
					System.out.println("Hier");
					System.out.println(key);
				}

			}
			Voronoi vo = new Voronoi(0.00001);
			List<GraphEdge> list = vo.generateVoronoi(vertices);
			SimpleWeightedGraph<String, DefaultWeightedEdge> g = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			for (String key : coords.keySet()) {
				g.addVertex(key);
			}
			for(final GraphEdge ge : list){
				//Normalisierte Distanz von 0 - 1. Je nÃ¤her zu Null desto nÃ¤her die Punkte. 1 = nicht auf dem Voronoi.
				Double distance = Math.sqrt(Math.pow((vertices[ge.site1].xPos-vertices[ge.site2].xPos), 2) + Math.pow((vertices[ge.site1].yPos-vertices[ge.site2].yPos), 2))/maxDistance;
				String edgeNode1 = sortedFeatures.get(ge.site1);
				String edgeNode2 = sortedFeatures.get(ge.site2);
				DefaultWeightedEdge e1 = g.addEdge(edgeNode1, edgeNode2);
				g.setEdgeWeight(e1, distance);
				if(!edgeVerteilung.containsKey(sortedFeatures.get(ge.site1)))
					edgeVerteilung.put(sortedFeatures.get(ge.site1), new ArrayList<GraphEdge>(){
						{
							add(ge);
						}
					});
				else
					if(!edgeVerteilung.get(sortedFeatures.get(ge.site1)).contains(ge))
						edgeVerteilung.get(sortedFeatures.get(ge.site1)).add(ge);

				if(!edgeVerteilung.containsKey(sortedFeatures.get(ge.site2)))
					edgeVerteilung.put(sortedFeatures.get(ge.site2), new ArrayList<GraphEdge>(){{add(ge);}});
				else
					if(!edgeVerteilung.get(sortedFeatures.get(ge.site2)).contains(ge))
						edgeVerteilung.get(sortedFeatures.get(ge.site2)).add(ge);
			}
			for(int k = 0; k<sortedFeatures.size(); k++){
				double tmpDistance = 0;
				for(int j = 0; j<sortedFeatures.size(); j++){
					double tmpDistanceSum = 0;
					List<DefaultWeightedEdge> pathEdges = DijkstraShortestPath.findPathBetween(g, sortedFeatures.get(k), sortedFeatures.get(j));
					for(DefaultWeightedEdge edge : pathEdges){
						tmpDistanceSum += g.getEdgeWeight(edge);
					}
					tmpDistance += tmpDistanceSum;
				}
				closenessCentrality.put(sortedFeatures.get(k), (1/tmpDistance));
			}

			for (String key : edgeVerteilung.keySet()) {
				ArrayList<Vertex> sortedEg = sortEdges(edgeVerteilung.get(key));
				nodeOrder.put(key, sortedEg);
			}
			for(String key : nodeOrder.keySet()){
				ArrayList<Vertex> tmpOrder = nodeOrder.get(key);
				edgeCount.put(key, tmpOrder.size());
				Double tmpSum = 0d;
				double tmpMinX = Double.MAX_VALUE;
				double tmpMinY = Double.MAX_VALUE;
				double tmpMaxX = Double.MIN_VALUE;
				double tmpMaxY = Double.MIN_VALUE;
				ArrayList<Double> tmpForm = new ArrayList<Double>();
				for(int k = 0; k<tmpOrder.size(); k++){
					Vertex tmpVertex = tmpOrder.get(k);
					if(tmpVertex.xPos>tmpMaxX)
						tmpMaxX = tmpVertex.xPos;
					if(tmpVertex.xPos<tmpMinX)
						tmpMinX = tmpVertex.xPos;
					if(tmpVertex.yPos>tmpMaxY)
						tmpMaxY = tmpVertex.yPos;
					if(tmpVertex.yPos<tmpMinY)
						tmpMinY = tmpVertex.yPos;
					Vertex nextVertex;
					if (k<(tmpOrder.size()-1))
						nextVertex = tmpOrder.get(k+1);
					else
						nextVertex = tmpOrder.get(0);
					tmpSum += (tmpVertex.yPos + nextVertex.yPos) * (tmpVertex.xPos - nextVertex.xPos);
				}
				tmpForm.add(tmpMinX);
				tmpForm.add(tmpMinY);
				tmpForm.add(tmpMaxX);
				tmpForm.add(tmpMaxY);
				form.put(key, tmpForm);
				flaeche.put(key, (Math.abs(tmpSum)/2));
			}
			for(String key : nodeOrder.keySet()){
				ArrayList<Vertex> tmpOrder = nodeOrder.get(key);
				edgeCount.put(key, tmpOrder.size());
			}
			// Gibt alle Werte aus
			for(String tmp : allFeatures.keySet()){
				//					if(nodeOrder.containsKey(tmp))
				//						System.out.println(tmp + ": " + nodeOrder.get(tmp));

				//					if(coords.containsKey(tmp))
				//						System.out.println(tmp + ": " + coords.get(tmp));
				//					if(isf.containsKey(tmp))
				//						System.out.println(tmp + ": " + isf.get(tmp));
				//					if(closenessCentrality.containsKey(tmp))
				//						System.out.println(tmp + ": " + closenessCentrality.get(tmp));
				//					if(edgeCount.containsKey(tmp))
				//						System.out.println(tmp + ": " + edgeCount.get(tmp));
				//					if(flaeche.containsKey(tmp))
				//						System.out.println(tmp + ": " + flaeche.get(tmp));
				//					if(frequencyValue.containsKey(tmp))
				//						System.out.println(tmp + ": " + frequencyValue.get(tmp));
				//					if(form.containsKey(tmp))
				//						for(Double tmpF : form.get(tmp)){
				//							System.out.println(tmp + ": " + tmpF);
				//						}
			}
			String finalVectorString = "";
			for(String tmp : allFeatures.keySet()){
				//					if(isf.containsKey(tmp)){
				//						System.out.println(tmp);
				//						System.out.println(featureCount.get(tmp));
				//						System.out.println(featureVerteilung.get(tmp).size());
				//						System.out.println(isf.get(tmp));
				//					}
				if(coords.containsKey(tmp))
					finalVectorString += (coords.get(tmp).xPos + ", " + coords.get(tmp).yPos + ", ");
				else
					finalVectorString += ("0, 0, ");

				if(isf.containsKey(tmp))
					finalVectorString += (isf.get(tmp) + ", ");
				else
					finalVectorString += ("0, ");

				if(closenessCentrality.containsKey(tmp))
					finalVectorString += (closenessCentrality.get(tmp) + ", ");
				else
					finalVectorString += ("0, ");

				if(edgeCount.containsKey(tmp))
					finalVectorString += (edgeCount.get(tmp) + ", ");
				else
					finalVectorString += ("0, ");

				if(flaeche.containsKey(tmp))
					finalVectorString += (flaeche.get(tmp) + ", ");
				else
					finalVectorString += ("0, ");

				if(frequencyValue.containsKey(tmp))
					finalVectorString += (frequencyValue.get(tmp) + ", ");
				else
					finalVectorString += ("0, ");

				if(form.containsKey(tmp)){
					ArrayList<Double> tmpForm = form.get(tmp);
					double width = Math.abs(tmpForm.get(0)-tmpForm.get(2));
					double height = Math.abs(tmpForm.get(1)-tmpForm.get(3));
					finalVectorString += (tmpForm.get(0) + ", " + tmpForm.get(1) + ", " + tmpForm.get(2) + ", " + tmpForm.get(3) + ", ");
					//						finalVectorString += (width + ", " + height + ", ");
				}
				else
					finalVectorString += ("0, 0, 0, 0, ");
				//				finalVectorString.subSequence(0, finalVectorString.length()-2);
			}
			//System.out.println(tmpDoc.getName());
//			try {
//				if(Integer.parseInt(tmpDoc.getName().split("_")[0]) < 13)
//					FileUtils.write(new File("guelichDataOutputTime"), (finalVectorString + "Dis\n"), true);
//				else
//					FileUtils.write(new File("guelichDataOutputTime"), (finalVectorString + "Epi\n"), true);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

			ImageVector iv = new ImageVector(aJCas, 0, aJCas.getDocumentText().length());
			//			System.out.println(finalVectorString);
			String[] das = finalVectorString.split(", ");
			DoubleArray da = new DoubleArray(aJCas, das.length);
			for(int k = 0; k<das.length; k++){
				//				System.out.println(das[k]);
				da.set(k, Double.parseDouble(das[k]));
			}
			String imageVecString = "";
			for (int i = 0; i < da.size(); i++) {
				imageVecString += da.get(i) + "\t";
			}

			//try {
			//	FileUtils.write(new File("ImageVectorsRun2.txt"), tmpDoc.getName() + "\t" + imageVecString + "\n", true);
			//} catch (IOException e) {
			//	// TODO Auto-generated catch block
			//	e.printStackTrace();
			//}
			iv.setEmbedding(da);
			//			iv.setValue();
			iv.addToIndexes(aJCas);

			//				for(VocabWord vb :vec.getLookupTable().getVocabCache().vocabWords()){
			//					org.hucompute.services.type.Word2Vec embedding = new org.hucompute.services.type.Word2Vec(aJCas);
			//					DoubleArray da = new DoubleArray(aJCas, tsn.getRow(i).length());
			//					for(int k=0; k<tsn.getRow(i).length(); k++){
			//						da.set(k, tsn.getRow(i).getDouble(k));
			//					}
			//					embedding.setEmbedding(da);
			//					embedding.setValue(vb.getWord());
			//					embedding.addToIndexes(aJCas);
			//					i++;
			//				}

	}

	/**
	 * Sortiert die Kanten und gibt die Reihenfolge der Knoten an
	 * @param edges
	 * @return Sortierte Folge der Knoten
	 */
	public static ArrayList<Vertex> sortEdges(ArrayList<GraphEdge> edges){
		ArrayList<Vertex> sortedEdges = new ArrayList<Vertex>();
		for (GraphEdge graphEdge : edges) {
			graphEdge.xPos1 = round(graphEdge.xPos1);
			graphEdge.yPos1 = round(graphEdge.yPos1);
			graphEdge.xPos2 = round(graphEdge.xPos2);
			graphEdge.yPos2 = round(graphEdge.yPos2);
		}

		int length = 0;
		sortedEdges.add(new Vertex(edges.get(0).xPos1, edges.get(0).yPos1));
		sortedEdges.add(new Vertex(edges.get(0).xPos2, edges.get(0).yPos2));
		edges.remove(0);
		while (sortedEdges.size() > length ) {
			Vertex tmpNode = sortedEdges.get(sortedEdges.size()-1);
			for(GraphEdge edge : edges){
				if((edge.xPos1 == tmpNode.xPos && edge.yPos1 == tmpNode.yPos)){
					sortedEdges.add(new Vertex(edge.xPos2, edge.yPos2));
					edges.remove(edge);
					break;
				}
				else{
					if((edge.xPos2 == tmpNode.xPos && edge.yPos2 == tmpNode.yPos)){
						sortedEdges.add(new Vertex(edge.xPos1, edge.yPos1));
						edges.remove(edge);
						break;
					}
				}
			}
			length++;
		}
		length = sortedEdges.size();
		while (sortedEdges.size() >= length) {
			Vertex tmpNode = sortedEdges.get(0);
			for(GraphEdge edge : edges){
				if((edge.xPos1 == tmpNode.xPos && edge.yPos1 == tmpNode.yPos)){
					sortedEdges.add(0, new Vertex(edge.xPos2, edge.yPos2));
					edges.remove(edge);
					break;
				}
				else{
					if((edge.xPos2 == tmpNode.xPos && edge.yPos2 == tmpNode.yPos)){
						sortedEdges.add(0, new Vertex(edge.xPos1, edge.yPos1));
						edges.remove(edge);
						break;
					}
				}
			}
			length++;
		}
		Vertex firstV = sortedEdges.get(0);
		Vertex lastV = sortedEdges.get(sortedEdges.size()-1);
		if(firstV.xPos == 0d){
			if(lastV.yPos == 0d)
				sortedEdges.add(new Vertex(0d, 0d));
			if(lastV.yPos == 100d)
				sortedEdges.add(new Vertex(0d, 100d));
		}
		if(firstV.yPos == 0d){
			if(lastV.xPos == 0d)
				sortedEdges.add(new Vertex(0d, 0d));
			if(lastV.xPos == 100d)
				sortedEdges.add(new Vertex(100d, 0d));
		}
		if(firstV.xPos == 100d){
			if(lastV.yPos == 0d)
				sortedEdges.add(new Vertex(100d, 0d));
			if(lastV.yPos == 100d)
				sortedEdges.add(new Vertex(100d, 100d));
		}
		if(firstV.yPos == 100d){
			if(lastV.xPos == 0d)
				sortedEdges.add(new Vertex(0d, 100d));
			if(lastV.xPos == 100d)
				sortedEdges.add(new Vertex(100d, 100d));
		}
		return sortedEdges;
	}

	public static double round(double toRound){
		double roundOff = Math.round(toRound * 10000000.0) / 10000000.0;
		return roundOff;
	}

}
