package de.cubenation.bedrock.core.plugin;


import java.beans.ConstructorProperties;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class PluginDescription {
    private String name;
    private String main;
    private String version;
    private String author;
    private Set<String> depends = new HashSet();
    private Set<String> softDepends = new HashSet();
    private File file = null;
    private String description = null;

    public String getName() {
        return this.name;
    }

    public String getMain() {
        return this.main;
    }

    public String getVersion() {
        return this.version;
    }

    public String getAuthor() {
        return this.author;
    }

    public Set<String> getDepends() {
        return this.depends;
    }

    public Set<String> getSoftDepends() {
        return this.softDepends;
    }

    public File getFile() {
        return this.file;
    }

    public String getDescription() {
        return this.description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDepends(Set<String> depends) {
        this.depends = depends;
    }

    public void setSoftDepends(Set<String> softDepends) {
        this.softDepends = softDepends;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PluginDescription that = (PluginDescription) o;

        if (!name.equals(that.name)) return false;
        if (!main.equals(that.main)) return false;
        if (!version.equals(that.version)) return false;
        if (author != null ? !author.equals(that.author) : that.author != null) return false;
        if (depends != null ? !depends.equals(that.depends) : that.depends != null) return false;
        if (softDepends != null ? !softDepends.equals(that.softDepends) : that.softDepends != null) return false;
        if (file != null ? !file.equals(that.file) : that.file != null) return false;
        return description != null ? description.equals(that.description) : that.description == null;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + main.hashCode();
        result = 31 * result + version.hashCode();
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (depends != null ? depends.hashCode() : 0);
        result = 31 * result + (softDepends != null ? softDepends.hashCode() : 0);
        result = 31 * result + (file != null ? file.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "PluginDescription(name=" + this.getName() + ", main=" + this.getMain() + ", version=" + this.getVersion() + ", author=" + this.getAuthor() + ", depends=" + this.getDepends() + ", softDepends=" + this.getSoftDepends() + ", file=" + this.getFile() + ", description=" + this.getDescription() + ")";
    }

    public PluginDescription() {
    }

    @ConstructorProperties({"name", "main", "version", "author", "depends", "softDepends", "file", "description"})
    public PluginDescription(String name, String main, String version, String author, Set<String> depends, Set<String> softDepends, File file, String description) {
        this.name = name;
        this.main = main;
        this.version = version;
        this.author = author;
        this.depends = depends;
        this.softDepends = softDepends;
        this.file = file;
        this.description = description;
    }
}
