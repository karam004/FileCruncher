package server;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import configutils.Constants;
import org.apache.log4j.Logger;
import com.google.api.client.http.FileContent;
import signup.Registration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by karamc on 11/14/15.
 */
public class Writer {
    private static final Logger logger = Logger.getLogger(Writer.class);
    private Drive service;

    public Writer(){
        try {
            this.service = Registration.getDriveService();
        }catch(Exception e){
            logger.error("SHIT HO GAYA !!");
        }
    }

    public Writer(Drive service){
        this.service = service;
    }

    private static String getFileId(Drive service, String fileName){
         try {
             FileList totalFiles = service.files().list().execute();
             for (File f : totalFiles.getItems()) {
                 if(f.getTitle().equals(fileName))
                    return f.getId();
             }
         }catch(IOException e){
             logger.error(e);
         }
        return null;
    }

    private static InputStream downloadFile(Drive service, String filename) {
        File file = null;
        try {
            String fileid = getFileId(service, filename);
            if (fileid == null) {
                return  null;
            }
            file = service.files().get(fileid).execute();
        } catch (GoogleJsonResponseException e){
            logger.info(e);
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            logger.error(e);
            return null;
        } catch (Exception e) {
            logger.error(e);
            return null;
        }

        if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
            try {

                HttpResponse resp =
                        service.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl()))
                                .execute();
                return resp.getContent();
            } catch (IOException e) {
                // An error occurred.
                e.printStackTrace();
                return null;
            }
        } else {
            // The file doesn't have any content stored on Drive.
            return null;
        }
    }

    boolean write(ByteBuffer byteBuffer, long id, long offsetIntoFile, int start, int length){
        try{
            RandomAccessFile outputFile = new RandomAccessFile(String.valueOf(id), "rw");//null;
            outputFile.setLength(Constants.CHUNK_SIZE);
            InputStream fileStream = downloadFile(this.service, String.valueOf(id));
            if(fileStream != null){
                int content;
                while((content = fileStream.read()) != -1){
                    outputFile.write(content);
                }
            }

            outputFile.seek(offsetIntoFile);
            byte[] readFromBuffer = new byte[length];
            byteBuffer.position(start);
            byteBuffer.get(readFromBuffer, 0, length);
            outputFile.write(readFromBuffer);
           if(fileStream == null){

               System.out.println("file not found");
               insertFile(this.service, String.valueOf(id), String.valueOf(id),
                       null, "*/*", String.valueOf(id));
           }
           else{
               System.out.println("updating file");
               updateFile(this.service, String.valueOf(id), String.valueOf(id), String.valueOf(id),
                       "*/*", String.valueOf(id),true);
           }

        }catch(Exception e){
            return false;
        }
        return true;
    }

    public void read(ByteBuffer byteBuffer, long id, long offsetChunk, long length){
        InputStream readStream = downloadFile(this.service, String.valueOf(id));
        try {
            assert readStream != null;
            readStream.skip(offsetChunk);
            byte[] readBuf = new byte[(int) length];
            readStream.read(readBuf, 0, (int)length);
            byteBuffer.put(readBuf);
        } catch (IOException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }


    }

    private static File updateFile(Drive service, String filename, String newTitle,
                                   String newDescription, String newMimeType, String newFilename, boolean newRevision) {
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
            File updatedFile = service.files().update(fileId, file, mediaContent).execute();

            return updatedFile;
        } catch (IOException e) {
            System.out.println("An error occurred: " + e);
            return null;
        }
    }



    private static File insertFile(Drive service, String title, String description,
                                   String parentId, String mimeType, String filename) {
        // File's metadata.
        File body = new File();
        body.setTitle(title);
        body.setDescription(description);
        body.setMimeType(mimeType);

        // Set the parent folder.
        /*if (parentId != null && parentId.length() > 0) {
            body.setParentsCollection(Arrays.asList(new File.ParentsCollection().setId(parentId)));
        }*/
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
}
