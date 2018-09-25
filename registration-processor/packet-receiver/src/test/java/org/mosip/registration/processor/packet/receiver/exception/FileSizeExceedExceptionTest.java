package org.mosip.registration.processor.packet.receiver.exception;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mosip.registration.processor.packet.receiver.exception.FileSizeExceedException;
import org.mosip.registration.processor.packet.receiver.exception.utils.IISPlatformErrorCodes;
import org.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

@RunWith(SpringRunner.class)
public class FileSizeExceedExceptionTest {
	private static final String FILE_SIZE_EXCEED_EXCEPTION = "This is file size exceed exception";
	private static final Logger log = LoggerFactory.getLogger(FileSizeExceedExceptionTest.class);

	@Mock
	private PacketReceiverService<MultipartFile, Boolean> packetHandlerService;

	@Test
	public void TestFileSizeExceedException() {

		FileSizeExceedException ex = new FileSizeExceedException(FILE_SIZE_EXCEED_EXCEPTION);

		Path path = Paths.get("src/test/resource/Client.zip");
		String name = "Client.zip";
		String originalFileName = "Client.zip";
		String contentType = "text/zip";
		byte[] content = null;
		try {
			content = Files.readAllBytes(path);
		} catch (IOException e1) {
			log.error(e1.getMessage());
		}
		MultipartFile file = new MockMultipartFile(name, originalFileName, contentType, content);

		Mockito.when(packetHandlerService.storePacket(file)).thenThrow(ex);
		try {

			packetHandlerService.storePacket(file);
			fail();

		} catch (FileSizeExceedException e) {
			assertThat("Should throw FileSizeExceed exception with correct error codes",
					e.getErrorCode().equalsIgnoreCase(IISPlatformErrorCodes.IIS_EPU_ATU_FILE_SIZE_EXCEED));
			assertThat("Should throw FileSizeExceed exception with correct messages",
					e.getErrorText().equalsIgnoreCase(FILE_SIZE_EXCEED_EXCEPTION));

		}
	}
}
