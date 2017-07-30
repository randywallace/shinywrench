package com.randywallace.shinywrench.model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
		this.credential_file_path = System.getProperty("user.home") + 
				File.separator + ".aws" + File.separator + "credentials";
		this.config_file_path = System.getProperty("user.home") + 
				File.separator + ".aws" + File.separator + "config";
		Path aws_config_dir = Paths.get(System.getProperty("user.home") + "/.aws");
		try {
			// TODO Add directory attributes
			Files.createDirectory(aws_config_dir);
		} catch (FileAlreadyExistsException e) {
			System.out.println(aws_config_dir.toString() + " already exists");
		} catch (Exception e) {
		    e.printStackTrace();
		    System.exit(2);
		}
		try {
			// TODO Add file attributes
			Files.createFile(Paths.get(this.config_file_path));
		} catch (FileAlreadyExistsException e) {
			System.out.println(this.config_file_path + " already exists");
		} catch (Exception e) {
		    e.printStackTrace();
		    System.exit(2);
		}
		try {
			// TODO Add file attributes
			Files.createFile(Paths.get(this.credential_file_path));
		} catch (FileAlreadyExistsException e) {
			System.out.println(this.credential_file_path + " already exists");
		} catch (Exception e) {
		    e.printStackTrace();
		    System.exit(2);
		}

		try {
			this.credential_ini.load(new FileReader(this.credential_file_path));
			this.config_ini.load(new FileReader(this.config_file_path));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(2);
		}
		// System.out.println(credential_ini.get("default").toString());
		for (Entry<String, Section> entry : this.config_ini.entrySet()) {
			String local_region = entry.getValue().get("region");
			if (local_region == null || local_region.isEmpty()) {
				local_region = "us-east-1";
			}
			String local_profile = entry.getKey();
			String[] local_profile_split = local_profile.split(" ");
			if (local_profile_split.length == 2) {
				local_profile = local_profile_split[1];
			}

			this.profileData.add(
					new Profile(
							local_profile,
							entry.getValue().get("aws_access_key_id"),
							entry.getValue().get("aws_secret_access_key"),
							local_region,
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
				//System.out.println(profile.getProfile().getValue() + " " + entry.getKey());
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
								entry.getKey(),
								entry.getValue().get("aws_access_key_id"),
								entry.getValue().get("aws_secret_access_key"),
								"us-east-1",
								null,
								entry.getValue().get("aws_session_token"),
								null,
								null,
								null,
								null));
			}
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

	public void saveConfig() {
		this.credential_ini = new Ini();
		this.config_ini = new Ini();

		for (Profile profile : this.profileData) {
			String profile_name = profile.getProfile().getValue();
			this.credential_ini.add(profile_name, "aws_access_key_id", profile.getAccess_key_id().getValue());
			this.credential_ini.add(profile_name, "aws_secret_access_key", profile.getSecret_access_key().getValue());
			this.credential_ini.add(profile_name, "aws_session_token", profile.getSession_token().getValue());
			try {
				this.credential_ini.store(new File(this.credential_file_path));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!profile_name.equals("default")) {
				profile_name = "profile " + profile_name;
			}
			this.config_ini.add(profile_name, "region", profile.getRegion().getValue());
			this.config_ini.add(profile_name, "mfa_serial", profile.getMfa_serial().getValue());
			this.config_ini.add(profile_name, "output", profile.getOutput().getValue());
			this.config_ini.add(profile_name, "role_arn", profile.getRole_arn().getValue());
			this.config_ini.add(profile_name, "source_profile", profile.getSource_profile().getValue());
			this.config_ini.add(profile_name, "expiration", profile.getExpiration().getValue());
			try {
				this.config_ini.store(new File(this.config_file_path));
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}
