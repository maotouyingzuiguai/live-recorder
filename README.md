# Live Recorder

## Project Overview
This project is a live streaming recording tool, ideal for scenarios requiring playback, editing, or reusing live stream content. Currently, it exclusively supports Douyin live streams.

## Features
1. **Real-time Monitoring and Downloading**: The tool automatically monitors and downloads video segments from specified Douyin live rooms as soon as they start.
2. **Live Link Parsing**: Users can copy the link to PotPlayer for playback, bypassing live room effects and reducing resource consumption.
3. **Utility Class**: This tool can be imported into projects as a utility class.
4. **Ease of Use**: Download the packaged JAR file and run it on any machine with Java installed, without needing ffmpeg.

### Core Class Descriptions
#### `LiveM3U8Downloader`
- A single-threaded downloader for live TS files.
- Implements the `DownloadProgress` interface to track download progress.
- Handles merging file segments and retries downloads if necessary.

#### `Recorder`
- A live stream recorder that allows customization of the live room video stream finder using `StreamFinder`.
- Monitors the live stream status and automatically downloads video segments.
- Offers immediate download and listening modes.

## Usage Guide
### Starting with JAR File
#### Starting with Parameters
1. Download the `jar` file.
2. Execute the following command: `java -jar liverecorder.jar <live stream URL> <start monitoring time (optional)> <end monitoring time (optional)> <monitoring interval (optional)> &`

#### Starting without Parameters
1. Download the `jar` file.
2. Run the JAR file: `java -jar liverecorder.jar`
3. Input the live stream URL when prompted in the console.

### Building from Source Code
1. Install Maven.
2. Execute `mvn package`.
3. Follow the steps outlined in the JAR file section.

### Using the Project as a Utility Class
1. Refer to the core class descriptions for integration instructions.

## Precautions
- Please adhere to relevant laws and regulations, as well as Douyin's terms of service, avoiding unauthorized acquisition or distribution of others' live stream content.
- Should you encounter issues during usage, please report them via GitHub issues or contact the project maintainers.

## Development and Contribution
We welcome developers to contribute to the improvement and optimization of this project. You can fork the project and submit pull requests. We also encourage users to report bugs, suggest new features, and share their experiences.
