package org.hucompute.textimager.uima.io.mediawiki;

import java.util.HashSet;
import java.util.Set;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.bitbucket.rkilinger.ged.Emotion;

class EmotionHelper {
    Set<String> disgustSet = new HashSet<String>();
    Set<String>	contemptSet = new HashSet<String>();
    Set<String> surpriseSet = new HashSet<String>();
    Set<String> fearSet = new HashSet<String>();
    Set<String> mourningSet = new HashSet<String>();
    Set<String> angerSet = new HashSet<String>();
    Set<String> joySet = new HashSet<String>();


	public EmotionHelper(JCas cas) {
		for (Emotion emotion : JCasUtil.select(cas, Emotion.class)) {
			if(emotion.getDisgust() == 1) {
				System.out.println(emotion.getCoveredText() +  " was found in disgust list");
				disgustSet.add(emotion.getCoveredText());
			}
			if(emotion.getContempt() == 1) {
				System.out.println(emotion.getCoveredText() +  " was found in contempt list");
				contemptSet.add(emotion.getCoveredText());
			}
			if(emotion.getSurprise() == 1) {
				System.out.println(emotion.getCoveredText() +  " was found in surprise list");
				surpriseSet.add(emotion.getCoveredText());
			}
			if(emotion.getFear() == 1) {
				System.out.println(emotion.getCoveredText() +  " was found in fear list");
				fearSet.add(emotion.getCoveredText());
			}
			if(emotion.getMourning() == 1) {
				System.out.println(emotion.getCoveredText() +  " was found in mourning list");
				mourningSet.add(emotion.getCoveredText());
			}
			if(emotion.getAnger() == 1) {
				System.out.println(emotion.getCoveredText() +  " was found in anger list");
				angerSet.add(emotion.getCoveredText());
			}
			if(emotion.getJoy() == 1) {
				System.out.println(emotion.getCoveredText() +  " was found in joy list");
				joySet.add(emotion.getCoveredText());
			}
        }
	}
    
    public String getDisgustString() {
		StringBuilder res = new StringBuilder();
		String delim= "";
		for (String entry : disgustSet) {
			res.append(delim).append(entry);
			delim = ",";
		}
		return res.toString();
    }
	public String getContemptString() {
		StringBuilder res = new StringBuilder();
		String delim= "";
		for (String entry : contemptSet) {
			res.append(delim).append(entry);
			delim = ",";
		}
		return res.toString();
    }
	public String getSurpriseString() {
		StringBuilder res = new StringBuilder();
		String delim= "";
		for (String entry : surpriseSet) {
			res.append(delim).append(entry);
			delim = ",";
		}
		return res.toString();
	}
	public String getFearString() {
		StringBuilder res = new StringBuilder();
		String delim= "";
		for (String entry : fearSet) {
			res.append(delim).append(entry);
			delim = ",";
		}
		return res.toString();
	}
	public String getMourningString() {
		StringBuilder res = new StringBuilder();
		String delim= "";
		for (String entry : mourningSet) {
			res.append(delim).append(entry);
			delim = ",";
		}
		return res.toString();
	}
	public String getAngerString() {
		StringBuilder res = new StringBuilder();
		String delim= "";
		for (String entry : angerSet) {
			res.append(delim).append(entry);
			delim = ",";
		}
		return res.toString();
	}
	public String getJoyString() {
		StringBuilder res = new StringBuilder();
		String delim= "";

		for (String entry : joySet) {
			res.append(delim).append("\"").append(entry).append("\"");
			delim = ",";
		}
		return res.toString();
	}

	public String buildStaticEmotionBarplotJS(){
		StringBuilder res = new StringBuilder();
		res.append("<html><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><style>\n");
		//res.append(".graph-tooltip {min-width: 300px !important; text-align: center;}");
		//res.append(".graph-tooltip {min-width: 300px !important; text-align: center;}");
		
		//CSS of the barplot 
		res.append(".tooltip_ddc_table td {border: solid 1px white;}\n");
  		res.append("#emotion_barplot {width: 1000px;height: 600px;margin: auto;}\n");
		res.append("svg {width: 100%;height: 100%;}\n");
		res.append(".bar {fill: #80cbc4;}\n");
		res.append("text {font-size: 12px;fill: #1E2126;}\n");
		res.append("path {stroke: gray;}\n");
		res.append("line {stroke: gray;}\n");
		res.append("text.title {font-size: 22px;font-weight: 600;}\n");
		res.append("text.label {font-size: 14px;font-weight: 400;}\n");

		res.append("</style></head><body><div id='emotion_barplot'><svg /></div></body><script src=\"https://d3js.org/d3.v5.min.js\"></script><script>\n");

		//TODO: Add length of the full text
		res.append("const textlen = 300;\n");
		
		//Add the data
		res.append("const word_distribution = [");
		
		res.append("{emotion: 'Ekel',");
		res.append("value:");
		res.append(disgustSet.size() + ",");
		res.append("words: ");
		res.append(getDisgustString());
		res.append("},");

		res.append("{emotion: 'Verachtung',");
		res.append("value:");
		res.append(contemptSet.size() + ",");
		res.append("words: ");
		res.append(getContemptString());
		res.append("},");

		res.append("{emotion: 'Ueberrschung',");
		res.append("value:");
		res.append(surpriseSet.size() + ",");
		res.append("words: ");
		res.append(getSurpriseString());
		res.append("},");

		res.append("{emotion: 'Furcht',");
		res.append("value:");
		res.append(fearSet.size() + ",");
		res.append("words: ");
		res.append(getFearString());
		res.append("},");

		res.append("{emotion: 'Trauer',");
		res.append("value:");
		res.append(mourningSet.size() + ",");
		res.append("words: ");
		res.append(getMourningString());
		res.append("},");

		res.append("{emotion: 'Wut',");
		res.append("value:");
		res.append(angerSet.size() + ",");
		res.append("words: ");
		res.append(getAngerString());
		res.append("},");

		res.append("{emotion: 'Freude',");
		res.append("value:");
		res.append(joySet.size() + ",");
		res.append("words: ");
		res.append(getJoyString());
		res.append("}");

		res.append("];");

		//D3js Code for the Barchart
		res.append("const svg = d3.select('svg');const svgContainer = d3.select('#emotion_barplot');const margin = 80;const width = 1000 - 2 * margin;const height = 600 - 2 * margin;const chart = svg.append('g').attr('transform', `translate(${margin}, ${margin})`); const xScale = d3.scaleBand().range([0, width]).domain(word_distribution.map((s) => s.emotion)).padding(0.4);const yScale = d3.scaleLinear().range([height, 0]).domain([0, textlen]);chart.append('g').attr('transform', `translate(0, ${height})`).call(d3.axisBottom(xScale));chart.append('g').call(d3.axisLeft(yScale));const barGroups = chart.selectAll().data(word_distribution).enter().append('g');\n");
		res.append("barGroups.append('rect').attr('class', 'bar').attr('x', (g) => xScale(g.emotion)).attr('y', (g) => yScale(g.value)).attr('height', (g) => height - yScale(g.value)).attr('width', xScale.bandwidth()).on('click', function (actual, i) {");
		
		//Action if a bar gets clicked (Zugriff auf zugehoeriges Objekt mit actual):
		res.append("const words = actual.words;const emotion = actual.emotion;alert(emotion);});");

		//Labels for Barchart
		res.append("svg.append('text').attr('class', 'label').attr('x', -(height / 2) - margin).attr('y', margin / 2).attr('transform', 'rotate(-90)').attr('text-anchor', 'middle').text('Vorkommen von Wörtern je Basisemotion');svg.append('text').attr('class', 'label').attr('x', width / 2 + margin).attr('y', height + margin * 1.5).attr('text-anchor', 'middle').text('Basisemotionen');svg.append('text').attr('class', 'title').attr('x', width / 2 + margin).attr('y', 45).attr('text-anchor', 'middle').text('Verteilung der Wörter auf die Basisemotionen');");

		return res.toString();
	}

}
