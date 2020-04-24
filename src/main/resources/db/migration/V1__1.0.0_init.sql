CREATE SCHEMA IF NOT EXISTS ch_notificator;

CREATE TYPE ch_notificator.notification_status AS ENUM('CREATED', 'ACTIVE', 'ARCHIVE');

CREATE TABLE ch_notificator.notification(
    name            CHARACTER VARYING                   NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE         NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE         NOT NULL,
    query_text      CHARACTER VARYING                   NOT NULL,
    period          CHARACTER VARYING                   NOT NULL,
    frequency       CHARACTER VARYING                   NOT NULL,
    alertChanel     CHARACTER VARYING                   NOT NULL,
    status          ch_notificator.notification_status  NOT NULL,
    template_type   CHARACTER VARYING                   NOT NULL,
    template_value  CHARACTER VARYING                   NOT NULL,

    groupByParams   CHARACTER VARYING,

    CONSTRAINT notification_pkey PRIMARY KEY (name)
);

CREATE TYPE ch_notificator.report_status AS ENUM('created', 'send', 'failed', 'skipped');

CREATE TABLE ch_notificator.report(
    id                  BIGSERIAL                       NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE     NOT NULL,
    notification_name   CHARACTER VARYING               NOT NULL,
    result              CHARACTER VARYING               NOT NULL,
    status              ch_notificator.report_status    NOT NULL,
    CONSTRAINT report_pkey PRIMARY KEY (id)
);

CREATE TYPE ch_notificator.channel_type AS ENUM('mail');

CREATE TABLE ch_notificator.channel(
    NAME                CHARACTER VARYING               NOT NULL,
    created_at          TIMESTAMP WITHOUT TIME ZONE     NOT NULL,
    type                ch_notificator.channel_type     NOT NULL,
    destination         CHARACTER VARYING               NOT NULL,
    subject             CHARACTER VARYING               NOT NULL,
    CONSTRAINT channel_pkey PRIMARY KEY (NAME)
);

