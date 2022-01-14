package org.hucompute.textimager.uima.io.mediawiki;

import org.apache.uima.jcas.JCas;

public class BarplotHelper {
    EmotionHelper eh;
    int totalLen;
    public BarplotHelper(JCas cas) {
        eh = new EmotionHelper(cas);
        totalLen = eh.disgustSet.size() + eh.contemptSet.size() + eh.surpriseSet.size() + eh.fearSet.size() + eh.mourningSet.size() + eh.angerSet.size() + eh.joySet.size();
    }
    public String buildStaticEmotionBarplotJS(){
		StringBuilder res = new StringBuilder();
		res.append("\n== ").append("Barplot").append(" ==\n");

		res.append("<html><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><style>\n");

		//CSS of the barplot
		res.append("#emotion_barplot {width: 1000px;height: 600px;margin: auto;}\n");
		res.append("svg {width: 100%;height: 100%;}\n");
		res.append(".bar {fill: #80cbc4;}\n");
		res.append("text {font-size: 12px;fill: #1E2126;}\n");
		res.append("path {stroke: gray;}\n");
		res.append("line {stroke: gray;}\n");
		res.append("text.title {font-size: 22px;font-weight: 600;}\n");
		res.append("text.label {font-size: 14px;font-weight: 400;}\n");
        res.append("</style>\n").append("</head>").append("<body>");
        res.append("<div id='emotion_barplot'>");
        res.append("<svg />");
        res.append("</div>");
        res.append("</body>");
        res.append("<script src=\"https://d3js.org/d3.v5.min.js\"></script>\n");
        res.append("<script>\n");

        res.append("const textlen = " + totalLen + ";\n");

		//Add the data
		res.append("const word_distribution = [");

		res.append("{emotion: 'Ekel',");
		res.append("value:");
		res.append(eh.disgustSet.size() + ",");
		res.append("words: ");
        res.append(eh.getEmotionStringListJS(eh.disgustSet));
		res.append("},\n");

		res.append("{emotion: 'Verachtung',");
		res.append("value:");
		res.append(eh.contemptSet.size() + ",");
		res.append("words: ");
        res.append(eh.getEmotionStringListJS(eh.contemptSet));
		res.append("},\n");

		res.append("{emotion: 'Ueberraschung',");
		res.append("value:");
        res.append(eh.surpriseSet.size() + ",");
		res.append("words: ");
        res.append(eh.getEmotionStringListJS(eh.surpriseSet));
		res.append("},\n");

		res.append("{emotion: 'Furcht',");
		res.append("value:");
        res.append(eh.fearSet.size() + ",");
		res.append("words: ");
        res.append(eh.getEmotionStringListJS(eh.fearSet));
		res.append("},\n");

		res.append("{emotion: 'Trauer',");
		res.append("value:");
        res.append(eh.mourningSet.size() + ",");
		res.append("words: ");
        res.append(eh.getEmotionStringListJS(eh.mourningSet));
		res.append("},\n");

		res.append("{emotion: 'Wut',");
		res.append("value:");
        res.append(eh.angerSet.size() + ",");
		res.append("words: ");
        res.append(eh.getEmotionStringListJS(eh.angerSet));
		res.append("},\n");

		res.append("{emotion: 'Freude',");
		res.append("value:");
        res.append(eh.joySet.size() + ",");
		res.append("words: ");
        res.append(eh.getEmotionStringListJS(eh.joySet));
		res.append("}");

		res.append("];\n");

		//D3js Code for the Barchart
		res.append("const svg = d3.select('svg');\n");
        res.append("const svgContainer = d3.select('#emotion_barplot');\n");
        res.append("const margin = 80;\n");
        res.append("const width = 1000 - 2 * margin;\n");
        res.append("const height = 600 - 2 * margin;\n");
        res.append("const chart = svg.append('g').attr('transform', `translate(${margin}, ${margin})`);\n");
        res.append("const xScale = d3.scaleBand().range([0, width]).domain(word_distribution.map((s) => s.emotion)).padding(0.4);\n");
        res.append("const yScale = d3.scaleLinear().range([height, 0]).domain([0, textlen]);\n");
        res.append("chart.append('g').attr('transform', `translate(0, ${height})`).call(d3.axisBottom(xScale));\n");
        res.append("chart.append('g').call(d3.axisLeft(yScale));\n");
        res.append("const barGroups = chart.selectAll().data(word_distribution).enter().append('g');\n");
		res.append("barGroups.append('rect').attr('class', 'bar').attr('x', (g) => xScale(g.emotion)).attr('y', (g) => yScale(g.value)).attr('height', (g) => height - yScale(g.value)).attr('width', xScale.bandwidth()).on('click', function (actual, i) {\n");

		//Action if a bar gets clicked (Zugriff auf zugehoeriges Objekt mit actual):
		res.append("const words = actual.words;\n");
        res.append("const emotion = actual.emotion;alert(words);});\n");

		//Labels for Barchart
		res.append("svg.append('text').attr('class', 'label').attr('x', -(height / 2) - margin).attr('y', margin / 2).attr('transform', 'rotate(-90)').attr('text-anchor', 'middle').text('Vorkommen von Wörtern je Basisemotion');\n");
        res.append("svg.append('text').attr('class', 'label').attr('x', width / 2 + margin).attr('y', height + margin * 1.5).attr('text-anchor', 'middle').text('Basisemotionen');\n");
        res.append("svg.append('text').attr('class', 'title').attr('x', width / 2 + margin).attr('y', 45).attr('text-anchor', 'middle').text('Verteilung der Wörter auf die Basisemotionen');\n");

        res.append("</script></html>\n");
		return res.toString();
	}
}
