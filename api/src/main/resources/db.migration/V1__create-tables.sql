CREATE TABLE users (
    id integer NOT NULL,
    username character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    name character varying(255),
    email character varying(255),
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted_at timestamp without time zone,
    active boolean DEFAULT true NOT NULL
);

CREATE TABLE installations (
    id SERIAL PRIMARY KEY,
    user_id BIGINT,
    installation_date TIMESTAMP NOT NULL,
    status VARCHAR(255) NOT NULL,
    total_power_generated DOUBLE PRECISION NOT NULL,
    total_energy_saved DOUBLE PRECISION NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

	CREATE TABLE weather_data (
    id SERIAL PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    condition VARCHAR(255) NOT NULL,
    temperature DOUBLE PRECISION NOT NULL,
    cloud_cover DOUBLE PRECISION NOT NULL,
    solar_irradiance DOUBLE PRECISION NOT NULL
);

	CREATE TABLE solar_panels (
    id SERIAL PRIMARY KEY,
    location VARCHAR(255) NOT NULL,
    current_power_generation DOUBLE PRECISION NOT NULL,
    total_power_generated DOUBLE PRECISION NOT NULL,
    efficiency DOUBLE PRECISION NOT NULL,
    status VARCHAR(255) NOT NULL,
    installation_id BIGINT,
    FOREIGN KEY (installation_id) REFERENCES installations(id)
);


CREATE TABLE batteries (
    id SERIAL PRIMARY KEY,
    current_charge DOUBLE PRECISION NOT NULL,
    capacity DOUBLE PRECISION NOT NULL,
    cycle_count INT NOT NULL,
    temperature DOUBLE PRECISION NOT NULL,
    health VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    installation_id BIGINT,
    FOREIGN KEY (installation_id) REFERENCES installations(id)
);
CREATE TABLE energy_consumption (
    id SERIAL PRIMARY KEY,
    installation_id BIGINT,
    timestamp TIMESTAMP NOT NULL,
    consumption DOUBLE PRECISION NOT NULL,
    grid_consumption DOUBLE PRECISION NOT NULL,
    solar_consumption DOUBLE PRECISION NOT NULL,
    battery_consumption DOUBLE PRECISION NOT NULL,
    FOREIGN KEY (installation_id) REFERENCES installations(id)
);