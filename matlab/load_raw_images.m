image_path = '/home/bugra/Dropbox/PolyClasses/Fall2012/CS6643-ComputerVision/Projects/Project_1/provided_images';
addpath(image_path);
filename = '0.raw';
raw_image = readraw(filename);

binary_image = raw_image;
binary_image(binary_image == 255) = 125;
binary_image(binary_image == 0) = 255;
binary_image(binary_image == 125) = 0;
threshold_binary_image = .5;
binary_image = im2bw(binary_image, threshold_binary_image);
imshow(binary_image)
imsave