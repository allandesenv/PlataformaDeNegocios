FROM ubuntu:latest
LABEL authors="allan"

ENTRYPOINT ["top", "-b"]