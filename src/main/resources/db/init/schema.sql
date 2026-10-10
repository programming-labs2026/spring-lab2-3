CREATE TABLE voting_user (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE voting (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    owner_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',

    CONSTRAINT fk_voting_owner FOREIGN KEY (owner_id) REFERENCES voting_user (id),
    CONSTRAINT ck_voting_status CHECK (status IN ('DRAFT', 'ACTIVE', 'STOPPED'))
);

CREATE TABLE candidate (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    voting_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,

    CONSTRAINT uq_candidate_id_voting UNIQUE (id, voting_id),
    CONSTRAINT fk_candidate_voting FOREIGN KEY (voting_id) REFERENCES voting (id)
);

CREATE TABLE vote (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    voting_id UUID NOT NULL,
    candidate_id UUID NOT NULL,
    user_id UUID NOT NULL,

    CONSTRAINT fk_vote_candidate_voting FOREIGN KEY (candidate_id, voting_id)
        REFERENCES candidate (id, voting_id),

    CONSTRAINT uq_vote_voting_user UNIQUE (voting_id, user_id),
    CONSTRAINT fk_vote_user FOREIGN KEY (user_id) REFERENCES voting_user (id),
    CONSTRAINT fk_vote_voting FOREIGN KEY (voting_id) REFERENCES voting (id)
);
