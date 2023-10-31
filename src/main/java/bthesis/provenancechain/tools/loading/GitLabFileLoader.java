package bthesis.provenancechain.tools.loading;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.RepositoryFile;
import org.openprovenance.prov.interop.Formats;
import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Implementation of the {@link IFileLoader} interface for loading files from GitLab repositories.
 *
 * @author Tomas Zobac
 */
public class GitLabFileLoader implements IFileLoader {
    /**
     * Loads a file from a GitLab repository based on the provided path and returns it converted to a Document.
     * This method assumes the provided path is a valid GitLab URL.
     *
     * @param path The URL path to the file in the GitLab repository.
     * @return The loaded Document.
     * @throws RuntimeException if there's an error in accessing the URL or interacting with the GitLab API.
     */
    @Override
    public Document loadFile(String path) {
        URL testurl;
        InteropFramework intF = new InteropFramework();

        try {
            testurl = new URL(path);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        String[] pathSegments = testurl.getPath().split("/");
        StringBuilder filePath = new StringBuilder();
        for (int i = 6; i != pathSegments.length; i++) filePath.append("/").append(pathSegments[i]);
        filePath.deleteCharAt(0);

        try (GitLabApi gitLabApi = new GitLabApi(testurl.getProtocol() + "://" + testurl.getHost(), "usLuqR2Km1yvSAkssYSe")) {
            RepositoryFile file = gitLabApi.getRepositoryFileApi().getFile(pathSegments[1] + "/" + pathSegments[2], filePath.toString(), pathSegments[5]);
            return intF.deserialiseDocument(new ByteArrayInputStream(file.getDecodedContentAsBytes()), Formats.ProvFormat.PROVN);
        } catch (IOException | GitLabApiException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the provided file name has a supported extension.
     *
     * @param name The name of the file.
     * @return True if the file's extension is supported, false otherwise.
     */
    @Override
    public boolean isSupportedExtension(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return false;
        }
        String ext = name.substring(lastIndexOf + 1);
        return SupportedExtensions.isSupported(ext);
    }
}
