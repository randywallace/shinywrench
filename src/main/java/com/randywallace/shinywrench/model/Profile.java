package com.randywallace.shinywrench.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Profile {

	private static Logger LOG = LoggerFactory.getLogger(Profile.class);

	private StringProperty profile;
	private StringProperty access_key_id;
	private StringProperty secret_access_key;
	private StringProperty region;
	private StringProperty output;
	private StringProperty session_token;
	private StringProperty role_arn;
	private StringProperty source_profile;
	private StringProperty mfa_serial;
	private SimpleStringProperty expiration;

	public Profile(
			String profile,
			String access_key_id,
			String secret_access_key,
			String region,
			String output,
			String session_token,
			String role_arn,
			String source_profile,
			String mfa_serial,
			String expiration) {

		this.profile = new SimpleStringProperty(profile);
		this.access_key_id = new SimpleStringProperty(access_key_id);
		this.secret_access_key = new SimpleStringProperty(secret_access_key);
		this.region = new SimpleStringProperty(region);
		this.output = new SimpleStringProperty(output);
		this.session_token = new SimpleStringProperty(session_token);
		this.role_arn = new SimpleStringProperty(role_arn);
		this.source_profile = new SimpleStringProperty(source_profile);
		this.mfa_serial = new SimpleStringProperty(mfa_serial);
		this.expiration = new SimpleStringProperty(expiration);

	}

	public StringProperty getProfile() {
		return this.profile;
	}

	public StringProperty getAccess_key_id() {
		return this.access_key_id;
	}

	public StringProperty getSecret_access_key() {
		return this.secret_access_key;
	}

	public StringProperty getRegion() {
		return this.region;
	}

	public StringProperty getOutput() {
		return this.output;
	}

	public StringProperty getSession_token() {
		return this.session_token;
	}

	public StringProperty getRole_arn() {
		return this.role_arn;
	}

	public StringProperty getSource_profile() {
		return this.source_profile;
	}

	public StringProperty getMfa_serial() {
		return this.mfa_serial;
	}

	public SimpleStringProperty getExpiration() {
		return this.expiration;
	}

	public void setProfile(StringProperty profile) {
		this.profile = profile;
	}

	public void setAccess_key_id(StringProperty access_key_id) {
		this.access_key_id = access_key_id;
	}

	public void setSecret_access_key(StringProperty secret_access_key) {
		this.secret_access_key = secret_access_key;
	}

	public void setRegion(StringProperty region) {
		this.region = region;
	}

	public void setOutput(StringProperty output) {
		this.output = output;
	}

	public void setSession_token(StringProperty session_token) {
		this.session_token = session_token;
	}

	public void setRole_arn(StringProperty role_arn) {
		this.role_arn = role_arn;
	}

	public void setSource_profile(StringProperty source_profile) {
		this.source_profile = source_profile;
	}

	public void setMfa_serial(StringProperty mfa_serial) {
		this.mfa_serial = mfa_serial;
	}

	public void setExpiration(Date expiration) {
		ZonedDateTime expiration_datetime = ZonedDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault());
		String expiration_string = DateTimeFormatter.ISO_DATE_TIME.format(expiration_datetime);
		this.expiration = new SimpleStringProperty(expiration_string);
	}

	public long getExpirationMinutesFromNow() {
		try {
			LocalDateTime expiration_zoned_datetime = LocalDateTime.parse(this.expiration.getValue(), DateTimeFormatter.ISO_DATE_TIME);
			long minutes_to_expire = Duration.between(LocalDateTime.now(), expiration_zoned_datetime).toMinutes();
			return minutes_to_expire;
		} catch (NullPointerException e) {
			LOG.warn(this.profile.getValue() + " has no expiration");
			return (long) 0;
		}
	}

	@Override
	public String toString() {
		return "Profile [profile=" + this.profile + ", access_key_id=" + this.access_key_id + ", secret_access_key=" + this.secret_access_key + ", region=" + this.region + ", output=" + this.output + ", session_token=" + this.session_token + ", role_arn=" + this.role_arn + ", source_profile=" + this.source_profile + ", mfa_serial=" + this.mfa_serial + ", expiration=" + this.expiration + "]";
	}

}
