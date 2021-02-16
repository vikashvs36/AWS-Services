# S3 - Simple Storage Service in AWS

Amazon Simple Storage Service (Amazon S3) is storage for the internet. We can use Amazon S3 to store and retrieve any amount of data at any time, from anywhere on the web. As we know, We need to store our data to make secure on any specific area which can be our local storage or any server storage. In this tutorial We will see how to store or retrieve our any type of data on Storage. Any type means that can be a file, an image or else.

## Local Storage 

Let see how to store or retrieve file from local system first.

As you can see in **LocalStorageController**, There has mentioned api's to upload or download file on behalf of our local system.

	// It has been created to upload a single file.
	POST : 	http://localhost:8080/api/local/s3/uploadFile
	
	// It has been created to upload multiple files
	POST : 	http://localhost:8080/api/local/s3/uploadFiles
	
	// It has been created to Download  a File
	GET : http://localhost:8080/api/local/s3/downloadFile/{fileNameWithExtention}
	
**Store Files :-**

There are two way to save File on local Storage. That is given below :

	final String fileName = StringUtils.cleanPath(file.getOriginalFilename());
			
	// First Way
	// Copy file to the target location (Replacing existing file with the same name) 
	final Path copyLocation = Paths.get(uploadDir+File.separator+fileName);
	Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
			
	// > Second Way
	// Get the file and save it somewhere
	byte[] bytes = file.getBytes();
	Path path = Paths.get(uploadDir + fileName);
	Files.write(path, bytes);
	
**Download File :-**

To Download any file we can use code as given below :

	@GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = storageService.loadAsResource(fileName);
			
        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            LOGGER.error("Could not determine file type.");
        }
			
        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }
		
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
File load as resource can be as given below :

	public Resource loadAsResource(String fileName) {
		Resource resource;
		Path path = Paths.get(uploadDir + fileName);
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		return resource;
	}