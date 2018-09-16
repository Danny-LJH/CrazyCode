#!/bin/sh
for file in `find . -name '*.mp4'`
do ffmpeg -i $file -vn -y -acodec copy -acodec pcm_s16le -f s16le -ac 1 -ar 16000 $file.pcm;
done