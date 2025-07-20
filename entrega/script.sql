CREATE UNLOGGED TABLE payment
(
    correlationId UUID PRIMARY KEY,
    amount        DECIMAL,
    requestedAt   TIMESTAMP,
    status        SMALLINT,
    processor     SMALLINT
);

-- select gen_random_uuid();

-- insert into payment(correlationId, amount, requestedAt, status, processor)
-- values (gen_random_uuid(), 12.2, now(), 1, 0);
-- insert into payment(correlationId, amount, requestedAt, status, processor)
-- values (gen_random_uuid(), 12.2, now(), 1, 1);

-- select * from payment;

