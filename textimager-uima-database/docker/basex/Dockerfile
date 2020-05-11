FROM ubuntu:xenial

RUN apt-get update
RUN apt-get install -y basex

RUN rm -rf /srv
RUN adduser --home /srv --disabled-password --disabled-login --uid 1984 --gecos "" basex && chown -R basex /srv
USER basex

EXPOSE 1984
CMD ["basexserver"]
