FROM solr:latest

WORKDIR /opt/solr/server/solr/
ADD uimadatabase /opt/solr/server/solr/uimadatabase
USER root
RUN chown -R solr /opt/solr/server/solr
USER solr
