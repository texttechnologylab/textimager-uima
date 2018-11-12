FROM python:2

WORKDIR /usr/src/app

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

COPY src/main/python/neuralnetwork_ner_rest ./

EXPOSE 80

ENV FLASK_APP neuralnetwork_ner_rest.py

CMD [ "python", "-m", "flask", "run", "-p", "80", "--host", "0.0.0.0" ]
