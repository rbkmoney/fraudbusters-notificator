DROP TABLE IF EXISTS ch_notificator.notification;

CREATE TABLE ch_notificator.notification_template
(
    id           CHARACTER VARYING           NOT NULL,
    name         CHARACTER VARYING           NOT NULL,
    type         CHARACTER VARYING           NOT NULL,
    skeleton     TEXT                        NOT NULL,
    query_text   CHARACTER VARYING           NOT NULL,
    basic_params CHARACTER VARYING,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT notification_pkey PRIMARY KEY (id)
);

CREATE TABLE ch_notificator.notification
(
    name        CHARACTER VARYING                  NOT NULL,
    subject     CHARACTER VARYING                  NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE        NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE        NOT NULL,
    period      CHARACTER VARYING                  NOT NULL,
    frequency   CHARACTER VARYING                  NOT NULL,
    channel     CHARACTER VARYING                  NOT NULL,
    status      ch_notificator.notification_status NOT NULL,
    template_id CHARACTER VARYING                  NOT NULL,

    CONSTRAINT fk_customer FOREIGN KEY (template_id) REFERENCES notification_template (id)
);




