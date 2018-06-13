FROM python:3

WORKDIR /usr/src/app

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt && python -m spacy download en && python -m spacy download de

COPY src/main/python/spacyrest/spacyrest.py ./

EXPOSE 80

ENV FLASK_APP spacyrest.py

CMD [ "python", "-m", "flask", "run", "-p", "80", "--host", "0.0.0.0" ]
