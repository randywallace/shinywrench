package com.randywallace.shinywrench.model;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map.Entry;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SystemProfile {

	private String credential_file_path;
	private String config_file_path;
	private ObservableList<Profile> profileData = FXCollections.observableArrayList();
	private Ini credential_ini;
	private Ini config_ini;

	public SystemProfile() {
		this.credential_ini = new Ini();
		this.config_ini = new Ini();
		switch (System.getProperty("os.name")) {
		case "Linux":
			this.credential_file_path = System.getenv("HOME") + "/.aws/credentials";
			this.config_file_path = System.getenv("HOME") + "/.aws/config";

			try {
				this.credential_ini.load(new FileReader(this.credential_file_path));
				this.config_ini.load(new FileReader(this.config_file_path));

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// System.out.println(credential_ini.get("default").toString());
			for (Entry<String, Section> entry : this.config_ini.entrySet()) {
				this.profileData.add(
						new Profile(
								entry.getKey().toString(),
								entry.getValue().get("aws_access_key_id"),
								entry.getValue().get("aws_secret_access_key"),
								entry.getValue().get("region"),
								entry.getValue().get("output"),
								entry.getValue().get("aws_session_token"),
								entry.getValue().get("role_arn"),
								entry.getValue().get("source_profile"),
								entry.getValue().get("mfa_serial"),
								entry.getValue().get("expiration")));
			}

			for (Entry<String, Section> entry : this.credential_ini.entrySet()) {
				Boolean updated = false;
				for (Profile profile : this.profileData) {
					System.out.println(profile.getProfile().getValue() + " " + entry.getKey());
					if (profile.getProfile().getValue().equals(entry.getKey())) {
						profile.setAccess_key_id(new SimpleStringProperty(entry.getValue().get("aws_access_key_id")));
						profile.setSecret_access_key(new SimpleStringProperty(entry.getValue().get("aws_secret_access_key")));
						profile.setSession_token(new SimpleStringProperty(entry.getValue().get("aws_session_token")));
						updated = true;
					}
				}
				if (!updated) {
					this.profileData.add(
							new Profile(
									entry.getKey().toString(),
									entry.getValue().get("aws_access_key_id"),
									entry.getValue().get("aws_secret_access_key"),
									null,
									null,
									entry.getValue().get("aws_session_token"),
									null,
									null,
									null,
									null));
				}
			}
			break;

		default:
			//System.out.println(this.credential_file_path + " " + this.config_file_path);
		}
		//System.out.println(this.profileData.toString());

	}

	public ObservableList<Profile> getProfileData() {
		return this.profileData;
	}

	public String getCredential_file_path() {
		return this.credential_file_path;
	}

	public void setCredential_file_path(String credential_file_path) {
		this.credential_file_path = credential_file_path;
	}

	public String getConfig_file_path() {
		return this.config_file_path;
	}

	public void setConfig_file_path(String config_file_path) {
		this.config_file_path = config_file_path;
	}
}
