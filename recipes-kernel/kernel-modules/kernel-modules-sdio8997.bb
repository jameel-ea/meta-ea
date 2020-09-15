SUMMARY = "Murata Wi-Fi driver for SDIO module 88w8997"
LICENSE = "BSD"

LIC_FILES_CHKSUM = "file://${S}/cyw-bt-patch/LICENCE.cypress;md5=cbc5f665d04f741f1e006d2096236ba7"

RRECOMMENDS_${PN} = "wireless-tools"

RDEPENDS_${PN} += "bash"

DEPENDS = "virtual/kernel"
do_configure[depends] += "make-mod-scripts:do_compile"

EXTRA_OEMAKE += " \
    KERNELDIR=${STAGING_KERNEL_BUILDDIR} \
"


SDIO_FILE_EXISTS="no"
#SDIO Source directory <- Points to BSP directory
SDIO_SOURCE_DIR="none"
sdio_file_size="4477181"
sdio_md5_sum="d0bc6b101214d0a4a232ef41e6efd3e2"
SDIO_FOLDER_NAME="SD-WLAN-SD-BT-8997-U16-MMC-W16.68.10.p56-16.26.10.p56-C4X16667_V4-MGPL"
SDIO_FILE_NAME="${SDIO_FOLDER_NAME}.zip"
SDIO_FOLDER_NAME_EXTRACT="SD-UAPSTA-BT-8997-U16-MMC-W16.68.10.p56-16.26.10.p56-C4X16667_V4-MGPL"

SRC_URI = " \
	git://github.com/murata-wireless/cyw-bt-patch;protocol=http;branch=zeus-gamera;destsuffix=cyw-bt-patch;name=cyw-bt-patch \
        file://${SDIO_FILE_NAME} \
	file://makefile.patch \
"


SRCREV_cyw-bt-patch="558f98ac67bd944afa003c247643fd47cc2dd3ab"

SRCREV_default = "${AUTOREV}"


S = "${WORKDIR}"
B = "${WORKDIR}"
DEPENDS = " libnl"


do_patch() {
	echo "------------------------------------------------------------------------------------------------------------------------"
	pwd
	echo "S-DIR: ${S}"
	echo "B-DIR: ${B}"
	echo "VKJB:: BUILD DIR: $BUILDDIR"

	#STEP-0: Set sdio source directory
	SDIO_SOURCE_DIR="${S}/../../../../../.."
	cd $SDIO_SOURCE_DIR
	pwd
	export SDIO_SOURCE_DIR=`pwd`
	echo "VKJB DEBUG:: SDIO source directory $SDIO_SOURCE_DIR"

	#STEP-1: Check for the presence of the SDIO Source file name
 	# Check for file size and md5sum
	echo "FILE NAME: ${SDIO_FILE_NAME}"
        if [ -e $SDIO_SOURCE_DIR/${SDIO_FILE_NAME} ]; then
		echo "DEBUG: File ${SDIO_FILE_NAME} EXISTS in $SDIO_SOURCE_DIR"
		filesize=$(stat --format=%s "$SDIO_SOURCE_DIR/${SDIO_FILE_NAME}")
		echo "File Size: ${filesize}"
		#file1_md5=$(md5sum -q "$SDIO_SOURCE_DIR/${SDIO_FILE_NAME}")
		file1_md5=$(md5sum "$SDIO_SOURCE_DIR/${SDIO_FILE_NAME}" | cut -d " " -f1)
		echo "MD5 SUM is : $file1_md5"

		if [ "${file1_md5}" = "${sdio_md5_sum}" ] && [ "${filesize}" = "${sdio_file_size}" ]; then
			echo "DEBUG: BOTH MD5SUM and file size MATCHES."
			SDIO_FILE_EXISTS="yes"
		else
			echo "MD5SUM / File Size DOES NOT MATCH."
			echo "with : md5 ${sdio_md5_sum}"
			echo "with : size ${sdio_file_size}"
		fi
	else
		echo "DEBUG: File ${SDIO_FILE_NAME} DOES NOT EXIST"
        fi

	#STEP 2: Copy the zip file to work dir and unzip it.
	if [ "$SDIO_FILE_EXISTS" = "yes" ]; then
		echo "COPYING it to work dir"
        	cp $SDIO_SOURCE_DIR/${SDIO_FILE_NAME} ${S}
		cd ${S}
		rm -rf ${SDIO_FOLDER_NAME}
		unzip ${SDIO_FILE_NAME}
	fi	

	if [ "$SDIO_FILE_EXISTS" = "yes" ]; then
		tar -xvf ${S}/SD-WLAN-SD-BT-8997-U16-MMC-W16.68.10.p56-16.26.10.p56-C4X16667_V4-MGPL.tar
		for i in `ls *.tgz`; do tar -xvf $i; done
		cd ${S}/SD-UAPSTA-BT-8997-U16-MMC-W16.68.10.p56-16.26.10.p56-C4X16667_V4-MGPL
        	patch -p1 < ${S}/makefile.patch
	fi

	echo "------------------------------------------------------------------------------------------------------------------------"
}


do_compile() {

	echo "Compiling"
	if [ "$SDIO_FILE_EXISTS" = "yes" ]; then
		echo "Compiling: murata-abcd"
        	echo "Testing Make        Display:: ${MAKE}"
	        echo "Testing bindir      Display:: ${bindir}"
	        echo "Testing base_libdir Display:: ${base_libdir}"
        	echo "Testing sysconfdir  Display:: ${sysconfdir}"
	        echo "Testing S  Display:: ${S}"
        	echo "Testing B  Display:: ${B}"
	        echo "Testing D  Display:: ${D}"
        	echo "vkjb :: ${WORKDIR}"
	        echo "PWD :: "
        	pwd

    		export ARCH=arm64
    		export CROSS_COMPILE=aarch64-poky-linux-

		# Change build folder to 8997 folder (wlan_src)
    		cd ${S}/${SDIO_FOLDER_NAME_EXTRACT}/wlan_src
		pwd
		oe_runmake build

		# Change build folder to 8997 folder (mbtc_src)
    		cd ${S}/${SDIO_FOLDER_NAME_EXTRACT}/mbtc_src
		pwd
		oe_runmake build

		# Change build folder to 8997 folder (mbt_src)
    		cd ${S}/${SDIO_FOLDER_NAME_EXTRACT}/mbt_src
		pwd
		oe_runmake build
	fi
}


do_install () {
	echo "Installing: "
	if [ "$SDIO_FILE_EXISTS" = "yes" ]; then
   		# install ko and configs to rootfs
   		install -d ${D}${datadir}/nxp_wireless

   		cp -rf ${S}/SD-UAPSTA-BT-8997-U16-MMC-W16.68.10.p56-16.26.10.p56-C4X16667_V4-MGPL/bin_sd8997 ${D}${datadir}/nxp_wireless
   		cp -rf ${S}/SD-UAPSTA-BT-8997-U16-MMC-W16.68.10.p56-16.26.10.p56-C4X16667_V4-MGPL/bin_sd8997_bt ${D}${datadir}/nxp_wireless
   		cp -rf ${S}/SD-UAPSTA-BT-8997-U16-MMC-W16.68.10.p56-16.26.10.p56-C4X16667_V4-MGPL/bin_sd8997_btchar ${D}${datadir}/nxp_wireless

	    	# Install NXP 8997-SDIO(1YM) firmware
    		install -d ${D}${nonarch_base_libdir}/firmware/nxp
    		install -m 0644 ${S}/FwImage/sd8997_bt_v4.bin ${D}${nonarch_base_libdir}/firmware/nxp
    		install -m 0644 ${S}/FwImage/sd8997_wlan_v4.bin ${D}${nonarch_base_libdir}/firmware/nxp
    		install -m 0644 ${S}/FwImage/sdsd8997_combo_v4.bin ${D}${nonarch_base_libdir}/firmware/nxp


	fi
}

FILES_${PN} = "${datadir}/nxp_wireless"

INSANE_SKIP_${PN} = "ldflags"
