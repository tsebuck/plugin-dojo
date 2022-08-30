FROM docker.elastic.co/elasticsearch/elasticsearch:8.2.2

ADD target/releases/doc-clone-plugin-1.0-SNAPSHOT.zip /doc-clone-plugin-1.0-SNAPSHOT.zip

RUN /usr/share/elasticsearch/bin/elasticsearch-plugin install --batch file:///doc-clone-plugin-1.0-SNAPSHOT.zip