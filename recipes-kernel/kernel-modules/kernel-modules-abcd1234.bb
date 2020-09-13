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


SRC_URI = " \
	git://github.com/murata-wireless/cyw-bt-patch;protocol=http;branch=zeus-gamera;destsuffix=cyw-bt-patch;name=cyw-bt-patch \
        file://SD-WLAN-SD-BT-8997-U16-MMC-W16.68.10.p56-16.26.10.p56-C4X16667_V4-MGPL.zip \
	file://WlanCalData_ext_DB_W8997_1YM_ES2_Rev_C.conf1 \
	file://ab.patch \
	file://makefile.patch \
"




SRCREV_cyw-bt-patch="558f98ac67bd944afa003c247643fd47cc2dd3ab"

SRCREV_default = "${AUTOREV}"


S = "${WORKDIR}"
B = "${WORKDIR}"
DEPENDS = " libnl"


do_patch() {

	echo "In patch of abcd"
	pwd
	echo "S-DIR: ${S}"
	echo "B-DIR: ${B}"
	tar -xvf ${S}/SD-WLAN-SD-BT-8997-U16-MMC-W16.68.10.p56-16.26.10.p56-C4X16667_V4-MGPL.tar
	for i in `ls *.tgz`; do tar -xvf $i; done
	cd ${S}/SD-UAPSTA-BT-8997-U16-MMC-W16.68.10.p56-16.26.10.p56-C4X16667_V4-MGPL
#        patch -p1 < ${S}/ab.patch
        patch -p1 < ${S}/makefile.patch
}


do_configure() {

	echo "In Configure of abcd"
#	pwd
#	echo "S-DIR: ${S}"
#	echo "B-DIR: ${B}"
#	tar -xvf ${S}/SD-WLAN-SD-BT-8997-U16-MMC-W16.68.10.p56-16.26.10.p56-C4X16667_V4-MGPL.tar
#	for i in `ls *.tgz`; do tar -xvf $i; done
#        patch -p1 < ${S}/ab.patch
}

do_compile() {
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

	# Change build folder to 8997 folder
    	cd ${S}/SD-UAPSTA-BT-8997-U16-MMC-W16.68.10.p56-16.26.10.p56-C4X16667_V4-MGPL/wlan_src
	pwd
	oe_runmake build

	# Change build folder to 8997 folder
    	cd ${S}/SD-UAPSTA-BT-8997-U16-MMC-W16.68.10.p56-16.26.10.p56-C4X16667_V4-MGPL/mbtc_src
	pwd
	oe_runmake build

	# Change build folder to 8997 folder
    	cd ${S}/SD-UAPSTA-BT-8997-U16-MMC-W16.68.10.p56-16.26.10.p56-C4X16667_V4-MGPL/mbt_src
	pwd
	oe_runmake build
}


do_install () {
	echo "Installing: "
   # install ko and configs to rootfs
   install -d ${D}${datadir}/mrvl_wireless

   cp -rf ${S}/SD-UAPSTA-BT-8997-U16-MMC-W16.68.10.p56-16.26.10.p56-C4X16667_V4-MGPL/bin_sd8997 ${D}${datadir}/mrvl_wireless
   cp -rf ${S}/SD-UAPSTA-BT-8997-U16-MMC-W16.68.10.p56-16.26.10.p56-C4X16667_V4-MGPL/bin_sd8997_bt ${D}${datadir}/mrvl_wireless
   cp -rf ${S}/SD-UAPSTA-BT-8997-U16-MMC-W16.68.10.p56-16.26.10.p56-C4X16667_V4-MGPL/bin_sd8997_btchar ${D}${datadir}/mrvl_wireless
}

FILES_${PN} = "${datadir}/mrvl_wireless"

INSANE_SKIP_${PN} = "ldflags"
