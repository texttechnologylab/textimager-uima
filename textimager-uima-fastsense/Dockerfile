FROM python:3.6

WORKDIR /usr/src/app

RUN apt-get update && apt-get install -y --no-install-recommends build-essential openjdk-8-jdk openjdk-8-jre-headless && rm -rf /var/lib/apt/lists/*
RUN pip install cython

COPY src/main/python/requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

COPY src/main/python/disambig_server/ ./disambig_server
COPY src/main/python/ned/ ./ned

COPY src/main/python/setup.py ./
RUN python setup.py install

EXPOSE 80

VOLUME [ "/model" ]

ENTRYPOINT [ "python", "disambig_server/disambig_server.py", "--model", "/model/", "--worker_count" ]
CMD [ "1" ]
