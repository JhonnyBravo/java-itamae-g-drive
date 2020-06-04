package java_itamae_g_drive.domain.repository.drive;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java_itamae_contents.domain.model.ContentsAttribute;
import java_itamae_g_auth.domain.model.AuthenticationAttribure;
import java_itamae_g_auth.domain.service.authentication.AuthenticationService;
import java_itamae_g_auth.domain.service.authentication.AuthenticationServiceImpl;

@RunWith(Enclosed.class)
public class DriveRepositoryTest {
    public static class 取得系メソッドのテスト {
        private static AuthenticationService authService;
        private static DriveRepository repository;
        private final String msgFormat = "id: %s name: %s mime_type: %s";

        @BeforeClass
        public static void setUpBeforeClass() throws Exception {
            ContentsAttribute contentsAttr = new ContentsAttribute();
            contentsAttr.setPath("src/test/resources/client_secret.json");
            contentsAttr.setEncoding("UTF-8");

            AuthenticationAttribure authAttr = new AuthenticationAttribure();
            authAttr.setUserName("test_user");
            authAttr.addScope(DriveScopes.DRIVE);

            authService = new AuthenticationServiceImpl();
            authService.authorize(contentsAttr, authAttr);
            repository = new DriveRepositoryImpl(authAttr);
        }

        @Test
        public void findAll実行時にファイルのリストを取得できること() throws Exception {
            List<File> fileList = repository.findAll();
            assertThat(fileList.isEmpty(), is(false));

            for (File file : fileList) {
                System.out.println(String.format(msgFormat, file.getId(),
                        file.getName(), file.getMimeType()));
            }

            System.out.println(fileList.size());
        }

        @Test
        public void findByParentId実行時に指定したディレクトリ配下に存在するファイルのリストを取得できること()
                throws Exception {
            String parentId = repository.getDriveRootId();
            List<File> fileList = repository.findByParentId(parentId);
            assertThat(fileList.isEmpty(), is(false));

            for (File file : fileList) {
                System.out.println(String.format(msgFormat, file.getId(),
                        file.getName(), file.getMimeType()));
            }

            System.out.println(fileList.size());
        }
    }

    public static class アップロードテスト {
        private static AuthenticationService authService;
        private static DriveRepository repository;

        @BeforeClass
        public static void setUpBeforeClass() throws Exception {
            ContentsAttribute contentsAttr = new ContentsAttribute();
            contentsAttr.setPath("src/test/resources/client_secret.json");
            contentsAttr.setEncoding("UTF-8");

            AuthenticationAttribure authAttr = new AuthenticationAttribure();
            authAttr.setUserName("test_user");
            authAttr.addScope(DriveScopes.DRIVE);

            authService = new AuthenticationServiceImpl();
            authService.authorize(contentsAttr, authAttr);
            repository = new DriveRepositoryImpl(authAttr);
        }

        @Test
        public void upload実行時にファイルが作成されること() throws Exception {
            String path = "src/test/resources/upload.txt";

            File actualFile = repository.upload(path);
            File expectFile = repository.findByFileId(actualFile.getId());

            assertThat(actualFile.getId(), is(expectFile.getId()));
            assertThat(actualFile.getName(), is(expectFile.getName()));

            repository.delete(actualFile.getId());
        }

        @Test
        public void upload実行時にディレクトリが作成されること() throws Exception {
            String path = "src/test/resources";

            File actualDir = repository.upload(path);
            File expectDir = repository.findByFileId(actualDir.getId());

            assertThat(actualDir.getId(), is(expectDir.getId()));
            assertThat(actualDir.getName(), is(expectDir.getName()));
            assertThat(actualDir.getMimeType(), is(expectDir.getMimeType()));

            repository.delete(actualDir.getId());
        }
    }

    public static class ダウンロードと更新のテスト {
        private static AuthenticationService authService;
        private static DriveRepository repository;

        private File uploadedDir;
        private File uploadedFile;

        @BeforeClass
        public static void setUpBeforeClass() throws Exception {
            ContentsAttribute contentsAttr = new ContentsAttribute();
            contentsAttr.setPath("src/test/resources/client_secret.json");
            contentsAttr.setEncoding("UTF-8");

            AuthenticationAttribure authAttr = new AuthenticationAttribure();
            authAttr.setUserName("test_user");
            authAttr.addScope(DriveScopes.DRIVE);

            authService = new AuthenticationServiceImpl();
            authService.authorize(contentsAttr, authAttr);
            repository = new DriveRepositoryImpl(authAttr);
        }

        @Before
        public void setUp() throws Exception {
            uploadedDir = repository.upload("src/test/resources");
            uploadedFile = repository.upload("src/test/resources/upload.txt",
                    uploadedDir.getId());
        }

        @After
        public void tearDown() throws Exception {
            repository.delete(uploadedFile.getId());
            repository.delete(uploadedDir.getId());
        }

        @Test
        public void findByParentId実行時に1件のファイルを取得できること() throws Exception {
            List<File> fileList = repository
                    .findByParentId(uploadedDir.getId());
            assertThat(fileList.size(), is(1));

            List<String> parentsList = fileList.get(0).getParents();
            assertThat(parentsList.size(), is(1));
            assertThat(parentsList.get(0), is(uploadedDir.getId()));
        }

        @Test
        public void update実行時にファイルが更新されること() throws Exception {
            String path = "src/test/resources/update.txt";
            java.io.File localFile = new java.io.File(path);
            File actualFile = repository.update(path, uploadedFile.getId());

            assertThat(actualFile.getId(), is(uploadedFile.getId()));
            assertThat(actualFile.getName(), is(localFile.getName()));
        }

        @Test
        public void update実行時にディレクトリが更新されること() throws Exception {
            String path = "src/test";
            java.io.File localDir = new java.io.File(path);
            File actualDir = repository.update(path, uploadedDir.getId());

            assertThat(actualDir.getId(), is(uploadedDir.getId()));
            assertThat(actualDir.getName(), is(localDir.getName()));
            assertThat(actualDir.getMimeType(), is(uploadedDir.getMimeType()));
        }

        @Test
        public void download実行時にディレクトリが作成されること() throws Exception {
            java.io.File download = new java.io.File("download");
            download.mkdir();

            java.io.File actualDir = repository.download("download",
                    uploadedDir.getId());
            java.io.File expectDir = new java.io.File(download.getName(),
                    uploadedDir.getName());

            assertThat(actualDir.isDirectory(), is(true));
            assertThat(actualDir.getCanonicalPath(),
                    is(expectDir.getCanonicalPath()));

            actualDir.delete();
            download.delete();
        }

        @Test
        public void download実行時にファイルが作成されること() throws Exception {
            java.io.File download = new java.io.File("download");
            download.mkdir();

            java.io.File actualFile = repository.download("download",
                    uploadedFile.getId());
            java.io.File expectFile = new java.io.File(download.getName(),
                    uploadedFile.getName());

            assertThat(actualFile.isFile(), is(true));
            assertThat(actualFile.getCanonicalPath(),
                    is(expectFile.getCanonicalPath()));

            actualFile.delete();
            download.delete();
        }
    }
}
