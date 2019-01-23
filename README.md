# image-colour-compression
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
* -o, --intermediate-images: outputs an image every iteration of the k-means clustering algorithm.

EXAMPLE: java CompressImage -i "random data point" -v --intermediate-images image-to-compress.jpg 8

# TODO

Create a UI

Add option to show temporary images and centroid colors, to show algorithm progess

Add advanced K Means Clustering options (verbose, intial centroid options...)
