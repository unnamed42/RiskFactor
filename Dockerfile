FROM node:12.13.0-alpine as build

WORKDIR /app

COPY package.json yarn.lock ./
RUN yarn install

COPY . ./

ARG BASE_URL
ENV BASE_URL=${BASE_URL}

RUN yarn run build

FROM nginx:1.17.0-alpine

COPY --from=build /app/dist /var/www/dist

RUN set -x; \
    addgroup -g 82 -S www-data; \
    adduser -u 82 -D -S -G www-data www-data && exit 0; exit 1
