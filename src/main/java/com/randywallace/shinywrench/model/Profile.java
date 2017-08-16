package com.randywallace.shinywrench.model;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;
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
	private StringProperty expiration;

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

	public String getProfile() {
		return this.profile.get();
	}

	public void setProfile(String profile) {
		this.profile.set(profile);
	}

	public StringProperty profileProperty() {
		return this.profile;
	}

	public String getAccessKeyId() {
		return this.access_key_id.get();
	}

	public void setAccessKeyId(String access_key_id) {
		this.access_key_id.set(access_key_id);
	}

	public StringProperty accessKeyIdProperty() {
		return this.access_key_id;
	}

	public String getSecretAccessKey() {
		return this.secret_access_key.get();
	}

	public void setSecretAccessKey(String secret_access_key) {
		this.secret_access_key.set(secret_access_key);
	}

	public StringProperty secretAccessKeyProperty() {
		return this.secret_access_key;
	}

	public String getRegion() {
		return this.region.get();
	}

	public void setRegion(String region) {
		this.region.set(region);
	}

	public StringProperty regionProperty() {
		return this.region;
	}

	public String getOutput() {
		return this.output.get();
	}

	public void setOutput(String output) {
		this.output.set(output);
	}

	public StringProperty outputProperty() {
		return this.output;
	}

	public String getSessionToken() {
		return this.session_token.get();
	}

	public void setSessionToken(String session_token) {
		this.session_token.set(session_token);
	}

	public StringProperty sessionTokenProperty() {
		return this.session_token;
	}

	public String getRoleArn() {
		return this.role_arn.get();
	}

	public void setRoleArn(String role_arn) {
		this.role_arn.set(role_arn);
	}

	public StringProperty roleArnProperty() {
		return this.role_arn;
	}

	public String getSourceProfile() {
		return this.source_profile.get();
	}

	public void setSourceProfile(String source_profile) {
		this.source_profile.set(source_profile);
	}

	public StringProperty sourceProfileProperty() {
		return this.source_profile;
	}

	public String getMfaSerial() {
		return this.mfa_serial.get();
	}

	public void setMfaSerial(String mfa_serial) {
		this.mfa_serial.set(mfa_serial);
	}

	public StringProperty mfaSerialProperty() {
		return this.mfa_serial;
	}

	public String getExpiration() {
		return this.expiration.get();
	}

	public StringProperty expirationProperty() {
		return this.expiration;
	}

	public void setExpiration(Date expiration) {
		ZonedDateTime expiration_datetime = ZonedDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault());
		String expiration_string = DateTimeFormatter.ISO_DATE_TIME.format(expiration_datetime);
		this.expiration.set(expiration_string);
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

	public static Callback<Profile, Observable[]> extractor() {
		return (Profile p) -> new Observable[]{
				p.profileProperty(),
				p.accessKeyIdProperty(),
				p.secretAccessKeyProperty(),
                p.regionProperty(),
				p.outputProperty(),
				p.sessionTokenProperty(),
				p.roleArnProperty(),
				p.sourceProfileProperty(),
				p.mfaSerialProperty(),
				p.expirationProperty()
		};
	}
}
