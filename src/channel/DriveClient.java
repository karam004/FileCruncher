package channel;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

import configutils.Constants;

public class DriveClient implements Client {
    private static final Logger logger = Logger.getLogger(DriveClient.class);
    private final Drive drive;

    public DriveClient(final Drive drive) {
        this.drive = drive;
    }

    @Override
    public String read(final ByteBuffer buffer, final long id,
            final long offset, final long length) {
        String fileId = getFileId(this.drive, Long.toString(id));
        if (fileId == null) return null;
        InputStream readStream = downloadFile(this.drive, String.valueOf(id));
        try {
            assert readStream != null;
            readStream.skip(offset);
            byte[] readBuf = new byte[(int) length];
            readStream.read(readBuf, 0, (int) length);
            buffer.put(readBuf);
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void write(final ByteBuffer buffer, final long id,
            final long offsetIntoFile, final long start, final long length) {

        try {
            RandomAccessFile outputFile = new RandomAccessFile(
                    String.valueOf(id), "rw");// null;
            outputFile.setLength(Constants.CHUNK_SIZE);
            InputStream fileStream = downloadFile(this.drive,
                    String.valueOf(id));
            if (fileStream != null) {
                int content;
                while ((content = fileStream.read()) != -1) {
                    outputFile.write(content);
                }
            }

            outputFile.seek(offsetIntoFile);
            byte[] readFromBuffer = new byte[(int) length];
            buffer.position((int) start);
            buffer.get(readFromBuffer, 0, (int) length);
            outputFile.write(readFromBuffer);
            if (fileStream == null) {

                System.out.println("file not found");
                insertFile(this.drive, String.valueOf(id), String.valueOf(id),
                        null, "*/*", String.valueOf(id));
            } else {
                System.out.println("updating file");
                updateFile(this.drive, String.valueOf(id), String.valueOf(id),
                        String.valueOf(id), "*/*", String.valueOf(id), true);
            }
            try {

                outputFile.close();
                fileStream.close();
                Files.delete(Paths.get("File://" + String.valueOf(id)));
            } catch (Exception e) {
                logger.error(e);
            }

        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }

    }

    private static File updateFile(final Drive service, final String filename,
            final String newTitle, final String newDescription,
            final String newMimeType, final String newFilename,
            final boolean newRevision) {
        try {
            // First retrieve the file from the API.
            String fileId = getFileId(service, filename);
            File file = service.files().get(fileId).execute();

            // File's new metadata.
            file.setTitle(newTitle);
            file.setDescription(newDescription);
            file.setMimeType(newMimeType);

            // File's new content.
            java.io.File fileContent = new java.io.File(newFilename);
            FileContent mediaContent = new FileContent(newMimeType, fileContent);

            // Send the request to the API.
            File updatedFile = service.files()
                    .update(fileId, file, mediaContent).execute();

            return updatedFile;
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
            return null;
        }
    }

    private static File insertFile(final Drive service, final String title,
            final String description, final String parentId,
            final String mimeType, final String filename) {
        // File's metadata.
        File body = new File();
        body.setTitle(title);
        body.setDescription(description);
        body.setMimeType(mimeType);

        // Set the parent folder.
        /*
         * if (parentId != null && parentId.length() > 0) {
         * body.setParentsCollection(Arrays.asList(new
         * File.ParentsCollection().setId(parentId))); }
         */
        body.setParents(new ArrayList<ParentReference>());

        // File's content.
        java.io.File fileContent = new java.io.File(filename);
        FileContent mediaContent = new FileContent(mimeType, fileContent);
        try {
            File file = service.files().insert(body, mediaContent).execute();

            // Uncomment the following line to print the File ID.
            // System.out.println("File ID: %s" + file.getId());

            return file;
        } catch (IOException e) {
            System.out.println("An error occured: " + e);
            return null;
        }
    }

    private static String getFileId(final Drive service, final String fileName) {
        try {
            FileList totalFiles = service.files().list().execute();
            for (File f : totalFiles.getItems()) {
                if (f.getTitle().equals(fileName)) {
                    return f.getId();
                }
            }
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
        return null;
    }

    private static InputStream downloadFile(final Drive service,
            final String filename) {
        File file = null;
        try {
            String fileid = getFileId(service, filename);
            if (fileid == null) {
                return null;
            }
            file = service.files().get(fileid).execute();
        } catch (GoogleJsonResponseException e) {
            logger.info(e);
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error(e);
            throw new RuntimeException(e);
        }

        if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
            try {

                HttpResponse resp = service.getRequestFactory()
                        .buildGetRequest(new GenericUrl(file.getDownloadUrl()))
                        .execute();
                return resp.getContent();
            } catch (IOException e) {
                // An error occurred.
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            // The file doesn't have any content stored on Drive.
            return null;
        }
    }
}
