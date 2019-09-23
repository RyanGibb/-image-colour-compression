# Image Colour Compression
Uses a [k-means clustering algorithm](https://en.wikipedia.org/wiki/K-means_clustering) to compress the number of colours used in an image.

Takes an image and converts it to an array of 3d data points (dimension for red, green, and blue). K-means clustering is used to find representative data points called centroids, in this 'color space'. Once found, the data points (representing colours) are assigned to their nearest centroid. This 'compressed color space' image is then output.

# Usage:

1. Navigate to src directory
2. Compile with "javac \*.java"
3. Run with "java CompressImage (options) <image input path> <No. colors> (output image path/directory)"

Options:
* -h, --help: gives info on usage and options\n
* -i, --initialization METHOD: specifies centroid initialization METHOD in the k-means clustering algorithm, where the METHOD is one of:
  * "random coordinate"
  * "random data point"
* -v, --verbose: makes the K Means Algorithm verbose, and output progress information.
* -o, --intermediate-images: outputs an image every iteration of the k-means clustering algorithm. (Requires a directory src/progress-images.)

EXAMPLE: java CompressImage -i "random data point" -v --intermediate-images image-to-compress.jpg 8

# Example

Windows XP background.

![xp.jpeg](examples/xp.jpeg)

### 16 colours

Running:
```
java CompressImage -v ~/Pictures/xp.jpeg 16
```
Gives:
```
Input Path: /home/ryan/Pictures/xp.jpeg	K-Means: 16	Output Path: /home/ryan/Pictures/xp-output-16.jpg
Properties:
	verbose:	true
Data points reassigned: 1158000		Centroids moved: 236
Data points reassigned: 210829		Centroids moved: 85
Data points reassigned: 112959		Centroids moved: 50
Data points reassigned: 81901		Centroids moved: 41
Data points reassigned: 76025		Centroids moved: 37
Data points reassigned: 56395		Centroids moved: 33
...
```

![xp-output-16.jpg](examples/xp-output-16.jpg)

### 8 colours

![xp-output-8.jpg](examples/xp-output-8.jpg)

### 4 colours

![xp-output-4.jpg](examples/xp-output-4.jpg)

### 3 colours

![xp-output-3.jpg](examples/xp-output-3.jpg)

### 2 colours

![p-output-2.jpg](examples/xp-output-2.jpg)

### 1 colour

The average colour of the pixels in the image.

![xp-output-1.jpg](examples/xp-output-1.jpg)

# TODO

Create a UI

Add option to show temporary images and centroid colors, to show algorithm progess

Add advanced K Means Clustering options (verbose, intial centroid options...)
