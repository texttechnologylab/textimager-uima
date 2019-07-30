package org.hucompute.textimager.uima.io.mediawiki;

/* We tried to make this class a subclass of MorphologicalFeatures, but when it came to printing the lemma pages, all attributes were lost (set to null). Maybe this is because the CAS got away. We could not figure it out, so we did a stupd read-only copy of MorphologicalFeatures. */

import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures;

public class EnhancedMorphologicalFeatures implements Comparable {

	private String token, value;
	private String gender, number, mcase, degree, verbForm, tense, mood, voice, definiteness, person, aspect, animacy, negative, numType, possessive, pronType, reflex;

	/** Create a new EnhancedMorphologicalFeatures from an existing one. */
	public EnhancedMorphologicalFeatures(String token, MorphologicalFeatures morphologicalFeatures) {
		MorphologicalFeatures f = morphologicalFeatures;

		this.token = token;
		this.value = f.getValue();
		this.gender = f.getGender();
		this.number = f.getNumber();
		this.mcase = f.getCase();
		this.degree = f.getDegree();
		this.verbForm = f.getVerbForm();
		this.tense = f.getTense();
		this.mood = f.getMood();
		this.voice = f.getVoice();
		this.definiteness = f.getDefiniteness();
		this.person = f.getPerson();
		this.aspect = f.getAspect();
		this.animacy = f.getAnimacy();
		this.negative = f.getNegative();
		this.numType = f.getNumType();
		this.possessive = f.getPossessive();
		this.pronType = f.getPronType();
		this.reflex = f.getReflex();

		if (value != null && !value.equals("_")) {
			// get values from value string
			for (String keyValue : value.split("\\|")) {
				String keyValueSplit[] = keyValue.split("=");
				if (keyValueSplit.length == 2) {
					String value = keyValueSplit[1];
					switch (keyValueSplit[0].toLowerCase()) {
						case "gender":       if (gender       == null) gender = value; break;
						case "number":       if (number       == null) number = value; break;
						case "case":         if (mcase        == null) mcase = value; break;
						case "degree":       if (degree       == null) degree = value; break;
						case "verbform":     if (verbForm     == null) verbForm = value; break;
						case "form":         if (verbForm     == null) verbForm = value; break;
						case "tense":        if (tense        == null) tense = value; break;
						case "mood":         if (mood         == null) mood = value; break;
						case "voice":        if (voice        == null) voice = value; break;
						case "definiteness": if (definiteness == null) definiteness = value; break;
						case "person":       if (person       == null) person = value; break;
						case "aspect":       if (aspect       == null) aspect = value; break;
						case "animacy":      if (animacy      == null) animacy = value; break;
						case "negative":     if (negative     == null) negative = value; break;
						case "numtype":      if (numType      == null) numType = value; break;
						case "possessive":   if (possessive   == null) possessive = value; break;
						case "prontype":     if (pronType     == null) pronType = value; break;
						case "reflex":       if (reflex       == null) reflex = value; break;
						default: System.out.println(" WARN | MediawikiWriter got a unknwn MorphologicalFeatures key: " + keyValueSplit[0]);
					}
				}
			}
		}

		// get a unified value string from variables
		value = "";
		if (gender       != null) value += "|gender=" + gender;
		if (number       != null) value += "|number=" + number;
		if (mcase        != null) value += "|mcase=" + mcase;
		if (degree       != null) value += "|degree=" + degree;
		if (verbForm     != null) value += "|verbform=" + verbForm;
		if (tense        != null) value += "|tense=" + tense;
		if (mood         != null) value += "|mood=" + mood;
		if (voice        != null) value += "|voice=" + voice;
		if (definiteness != null) value += "|definiteness=" + definiteness;
		if (person       != null) value += "|person=" + person;
		if (aspect       != null) value += "|aspect=" + aspect;
		if (animacy      != null) value += "|animacy=" + animacy;
		if (negative     != null) value += "|negative=" + negative;
		if (numType      != null) value += "|numtype=" + numType;
		if (possessive   != null) value += "|possessive=" + possessive;
		if (pronType     != null) value += "|prontype=" + pronType;
		if (reflex       != null) value += "|reflex=" + reflex;
		if (value.length() > 0 && value.charAt(0) == '|') {
			value = value.substring(1, value.length());
		}

		// test for empty object
		if (value.equals("")) {
			throw new IllegalArgumentException("morphologicalFeatures must have at least one value set");
		}
	}

	public int compareTo(Object obj) {
		return equals(obj) ? 0 : -1;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof EnhancedMorphologicalFeatures)) return false;
		EnhancedMorphologicalFeatures f = (EnhancedMorphologicalFeatures) obj;
		return (f.getGender() == null ? getGender() == null : f.getGender().equals(getGender())) &&
			(f.getNumber() == null ? getNumber() == null : f.getNumber().equals(getNumber())) &&
			(f.getCase() == null ? getCase() == null : f.getCase().equals(getCase())) &&
			(f.getDegree() == null ? getDegree() == null : f.getDegree().equals(getDegree())) &&
			(f.getVerbForm() == null ? getVerbForm() == null : f.getVerbForm().equals(getVerbForm())) &&
			(f.getTense() == null ? getTense() == null : f.getTense().equals(getTense())) &&
			(f.getMood() == null ? getMood() == null : f.getMood().equals(getMood())) &&
			(f.getVoice() == null ? getVoice() == null : f.getVoice().equals(getVoice())) &&
			(f.getDefiniteness() == null ? getDefiniteness() == null : f.getDefiniteness().equals(getDefiniteness())) &&
			(f.getPerson() == null ? getPerson() == null : f.getPerson().equals(getPerson())) &&
			(f.getAspect() == null ? getAspect() == null : f.getAspect().equals(getAspect())) &&
			(f.getAnimacy() == null ? getAnimacy() == null : f.getAnimacy().equals(getAnimacy())) &&
			(f.getNegative() == null ? getNegative() == null : f.getNegative().equals(getNegative())) &&
			(f.getNumType() == null ? getNumType() == null : f.getNumType().equals(getNumType())) &&
			(f.getPossessive() == null ? getPossessive() == null : f.getPossessive().equals(getPossessive())) &&
			(f.getPronType() == null ? getPronType() == null : f.getPronType().equals(getPronType())) &&
			(f.getReflex() == null ? getReflex() == null : f.getReflex().equals(getReflex()));
	}

	public String getToken() { return token; }
	public String getGender() { return gender; }
	public String getNumber() { return number; }
	public String getCase() { return mcase; }
	public String getDegree() { return degree; }
	public String getVerbForm() { return verbForm; }
	public String getTense() { return tense; }
	public String getMood() { return mood; }
	public String getVoice() { return voice; }
	public String getDefiniteness() { return definiteness; }
	public String getPerson() { return person; }
	public String getAspect() { return aspect; }
	public String getAnimacy() { return animacy; }
	public String getNegative() { return negative; }
	public String getNumType() { return numType; }
	public String getPossessive() { return possessive; }
	public String getPronType() { return pronType; }
	public String getReflex() { return reflex; }
	public String getValue() { return value; }

	public int hashCode() {
		return ("|" + gender + "|" + number + "|" + mcase + "|" + degree + "|" + verbForm + "|" + tense + "|" + mood + "|" + voice + "|" + definiteness + "|" + person + "|" + aspect + "|" + animacy + "|" + negative + "|" + numType + "|" + possessive + "|" + pronType + "|" + reflex + "|").hashCode();
	}

	public String toString() {
		return "EnhancedMorphologicalFeatures[" + token + ":" + value + "]";
	}

}

