ALTER TABLE ch_notificator.notification
    DROP CONSTRAINT notification_pkey;
DROP TABLE IF EXISTS ch_notificator.notification;

CREATE TABLE ch_notificator.notification_template
(
    id           SERIAL                      NOT NULL,
    name         CHARACTER VARYING           NOT NULL,
    type         CHARACTER VARYING           NOT NULL,
    skeleton     TEXT                        NOT NULL,
    query_text   CHARACTER VARYING           NOT NULL,
    basic_params CHARACTER VARYING,
    created_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT notification_template_pkey PRIMARY KEY (id)
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
    template_id SERIAL                             NOT NULL,

    CONSTRAINT notification_pkey PRIMARY KEY (name),
    CONSTRAINT fk_notification_template FOREIGN KEY (template_id) REFERENCES notification_template (id)
);




