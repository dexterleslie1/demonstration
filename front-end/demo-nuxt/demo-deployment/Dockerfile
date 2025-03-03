FROM node:16.14.0

ENV NODE_ENV=production

RUN mkdir /nuxt

WORKDIR /nuxt

ENV HOST 0.0.0.0

EXPOSE 3000

COPY ./.nuxt /nuxt/.nuxt
COPY ./nuxt.config.js /nuxt
COPY ./package.json /nuxt

RUN npm install --no-package-lock --registry=https://registry.npm.taobao.org

CMD ["npm", "start"]
