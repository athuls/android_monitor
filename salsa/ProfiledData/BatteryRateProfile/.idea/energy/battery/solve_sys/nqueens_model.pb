
E
PlaceholderPlaceholder*
shape:���������*
dtype0
m
@dnn/input_from_feature_columns/input_layer/demo1.Nqueens/ToFloatCastPlaceholder*

SrcT0*

DstT0
�
>dnn/input_from_feature_columns/input_layer/demo1.Nqueens/ShapeShape@dnn/input_from_feature_columns/input_layer/demo1.Nqueens/ToFloat*
T0*
out_type0
z
Ldnn/input_from_feature_columns/input_layer/demo1.Nqueens/strided_slice/stackConst*
dtype0*
valueB: 
|
Ndnn/input_from_feature_columns/input_layer/demo1.Nqueens/strided_slice/stack_1Const*
valueB:*
dtype0
|
Ndnn/input_from_feature_columns/input_layer/demo1.Nqueens/strided_slice/stack_2Const*
valueB:*
dtype0
�
Fdnn/input_from_feature_columns/input_layer/demo1.Nqueens/strided_sliceStridedSlice>dnn/input_from_feature_columns/input_layer/demo1.Nqueens/ShapeLdnn/input_from_feature_columns/input_layer/demo1.Nqueens/strided_slice/stackNdnn/input_from_feature_columns/input_layer/demo1.Nqueens/strided_slice/stack_1Ndnn/input_from_feature_columns/input_layer/demo1.Nqueens/strided_slice/stack_2*
shrink_axis_mask*

begin_mask *
ellipsis_mask *
new_axis_mask *
end_mask *
Index0*
T0
r
Hdnn/input_from_feature_columns/input_layer/demo1.Nqueens/Reshape/shape/1Const*
value	B :*
dtype0
�
Fdnn/input_from_feature_columns/input_layer/demo1.Nqueens/Reshape/shapePackFdnn/input_from_feature_columns/input_layer/demo1.Nqueens/strided_sliceHdnn/input_from_feature_columns/input_layer/demo1.Nqueens/Reshape/shape/1*
T0*

axis *
N
�
@dnn/input_from_feature_columns/input_layer/demo1.Nqueens/ReshapeReshape@dnn/input_from_feature_columns/input_layer/demo1.Nqueens/ToFloatFdnn/input_from_feature_columns/input_layer/demo1.Nqueens/Reshape/shape*
T0*
Tshape0
�
1dnn/input_from_feature_columns/input_layer/concatIdentity@dnn/input_from_feature_columns/input_layer/demo1.Nqueens/Reshape*
T0
�
dnn/hiddenlayer_0/kernel/part_0Const*�
value�B�("���o>g_�K�8>��=�_b>8���?�=�
=�t̽�h���[>��=��=��<0!�>��`���xk>|S����X>0Z >�8�>��=�[E��PT>z>�D��x�==,y��Z�>Tur��?���>BR���k��5d��}[=��=�)��]��>�s<*@��ET>nV�=<�S>qƃ=2w >w1g�f�S��>���%҇=���*�=�Ї��E�>f����K>8t��c(>    �S�=�꺽xf�>>��
yD>�a�~���s�:������b>^��px�>Y�c�4P8��>�w>M��ӝ�>R��������{���"�WZT�wF->���=lJ���*�0��>苃<R�/>S�p>�c��^��Fr��ӽ[��=P�u<�i=*50��r4�1��> �4��I>���>B�!��'[����%�>���_C�<��^>�c�>T$�=�Q��_����>��=ۗT��ۿ>jʞ>��=�Y��ſ;g��>��=�N�`�P=�8V=����{o��#��h�˼w>��z=���=�Dս륏��ûN#��`۽z�h�|�����xm�SO�>bF8>N[�>>7>����G� >�9�=Z�+�8s�<0G`>���=��>�D>�ؠ<�Z>�~5�Tr����>y��=ʉ޽���=���>`�F�Y=<�hP��t۽�{=�O>���=pS`>����(HW���=v�=��J>"��	�>aX;NP�H3�<��=��K<pc�>���=�Ё>Q@"�l=t>�.�><M�=��;�P]C>K�A�H�s<�[f>R<��R�=T�c>3�Ž��>S��>�(�;eo1������D)>%�>�)��*=    ^���*^>��N=ā��<�>`A>D"=+qp������H��G�
>�i>��O���\>J�����i>P_�j>^��>��Z>6�g=��)>�Ѹ��P�>=⛾k.�>nQ�=�O=-��(�>���<)�=AY>`�.>�,�<�bQ�-~B�������R>��=�"M�����/�9e�>��O>}�
����A��;>,�>y�=tͽ*_½tG>hLý����_RL�i<��c=q�0>�6 >���=W9���>�3����.����>��(>赠�W�=�	�=�,r�bE#���=!�+����?�c=�e=������=������>GЃ�=�|S>�"h�xm��VY���'���`>Tߘ>�ǈ>Z������>�D �>�[>��z>8��=��#�أ��D����e��{)��Q��k�m>�錽P�(>�P=��)�=�(�.+�>5</>�8��_=>#�=TD�����;�X��
��|��=^s>�sj=\J�>���X-1��1�����=�P�<��>`a��L���0%I�ѽ<D��D�>i��!q>!܈>݂��ċ ���`��>�n>"9>_5=��l����	S=#�>Ф���6�>0,�<*��=��P>�x�Ry7���1>cf�QP��F;!�h�� Y�=^�,>U�ԽGH��@�=3+�=xU)�}n�>��>    ��>�Y�����=nH���n>�\Ｌ��=�?���'>���=2.Լ�	w>��7�i�m=A�F��>ּ���>�?�>�
�=�Ƚ�&�>���Z>�"�<�X=�@>p�=,Ŕ>`��>��=8�=  Q=�Q�=X���LtC=�|�>�Ϝ>��=��!�*L> �>�>�+f�M'��}�s>�愼H�P>ʪR�ZJ*�z#E��k���!���	��W��=�<f�ɽ��e=2�5�΀~���\�4�g>]�\>��=��l>���%!.����N�_9s>0����>��>��:������Մ>u:%��>�w�X��<`7H����=<$�>&$�8̳���>���>���=��:
e'��F>N�>�ݎ>�=xт>���o��>	�=B�<>3��di�p[=1����ֽU�|&��F�ý�^=�:H>�1Q�u[�>�օ>�?_�����3>>\&��QO>����[==.���ݗ��G>��o<��P
�8%¼��h�J򰾗k���%>sG�=w���K-�>��W��>���>ގe>>u�?[�>�r>���=IB�ρl�CՀ��c^�F6����^�3�>�҉��P�>��=޿���"�>S�>H_\<���<�[���	=�k4�X�=�����1�Sw= ӈ>j�l9w=��>}E&��M}>pt���!���AT>��'�    ���<)!��F	�=z�$=^��>8��𥇽(PP���K>h�>��A>�4/>TK���Ƽ��]>��>�e>��f>JX���.�>�Ď>��X�����F>�����2f=DT���G=�a�>Sb�<�k�>6�H�3c�g�o>��4>�b�n :����H`ڽ��p��+d�yV<b��>��L=�|> ���/>x<��̐>Ǟ*>��=�g>h�>��A>D��=��=�-�>��7>��j=uU�a�V�րD�Mq>n^l;`R>�_>
��@��C%��ν����q��i�~�=g��=@�y>6HX>R�U>��m>pz"=�M�Ŀ8�o�>ETj>c�=�X� -���?������[y�0�=�1V�$䂾'=�qc=�mC>'�����= �н�z=��M>��ܽ���<ᒎ�(�n��׼�����ԕ��$>`Gs�`�>��ӽ�S,�ǡh<Zj�>��P;���)�=�z>0-���M�Ɲ>[q8�l��֮ڽ�]>�. <���=(�u>ʚr�����dX>�>>�>�3ҽ���B>V��\v��}���e=ֽ�C>�g�����&>XB�<P�=    �Ĉ><�K>�Lx<N����=d9j�p�8>iG:<��B>��P>�1��W�>t@�>�qw>��Hi��67��$�콈�D�p�|>ȣ�>e�;�    �^½�sp>4��=@�>�q�>8�>Ȝ��
T���>�c>2�?�0TP>�=.i!>|�O>X�\=�F��3�=�̓>i����U>R:>$�j>��e(��&Y�xӷ���m��&�t�t��i>�)>s�R�����N�>W��<f\潀��=�o���P��>ȼ�<@����8=��P>KW!��r�>�r!>���=���k�<��S>rߒ�_��pȽ    ��>xs>�&z��Q�8��$��=4�����<��z>
7~��0��;=�k�>&�˽��>�
�=|,�>�8���A�>    czX���#�[>z��>`���r����>2��r�>?������Q<AR�L�;��=b0o���{��X�De�=,z��    A���IDg������T�=OH>�b>��V�>��2�Й�=��e4����=�罤��=p�2= "^=(<    |�.> .=O(�<8#>��>�<�=hb��&��>��=�*�����G �x>|>�y>�k�<>(Pc=q �����C_�    � >��>���=��E��7P�(��0�۽�N&��F��[�n�8JA> CD>z��>��>�x��P�˵r�1�>�R�Z�(<�/�h�>쒩=��x�Y����=g�3�G8L�X =MY�=�� ���p�>�9��jy>�k`>��> �%>t�i>�p���6���]>�P�;1�7���F>5��>�[>��>sz���[>Fi@>��P>    ��/��A��Ք�l�ͽ�d�=�_K>�����pw�=�4>$t>d�)>���aj>�>J�`���G>�<�=�o����ֽ�OR�Qw��W������0��J`�uH�.�Z�ЭB��/gn�7K���9=���=��=��<��><�[>�?,�\��=��[;��>&HU�<��=�
�8!>pJ=�>��W>    hBG=��A=�Ű<xǔ<���>�ɠ��s���ً>$���|�>�c=>�#N>d���$��\��=B�h<T��>�E���`�=HPd>8�/���C�e�=.��Á��:�*
dtype0
�
$dnn/hiddenlayer_0/kernel/part_0/readIdentitydnn/hiddenlayer_0/kernel/part_0*
T0*2
_class(
&$loc:@dnn/hiddenlayer_0/kernel/part_0
�
dnn/hiddenlayer_0/bias/part_0Const*�
value�B�("��>=    �=�=o��=���=�d�=�R=    ��D�=    o���    ���    ��=L-����=    �N�    Q�=@<���ѩ=�¯=^��=        =    ��μ4O�=�>�=        r[S���=�޼�po�SM�=*
dtype0
�
"dnn/hiddenlayer_0/bias/part_0/readIdentitydnn/hiddenlayer_0/bias/part_0*
T0*0
_class&
$"loc:@dnn/hiddenlayer_0/bias/part_0
S
dnn/hiddenlayer_0/kernelIdentity$dnn/hiddenlayer_0/kernel/part_0/read*
T0
�
dnn/hiddenlayer_0/MatMulMatMul1dnn/input_from_feature_columns/input_layer/concatdnn/hiddenlayer_0/kernel*
T0*
transpose_a( *
transpose_b( 
O
dnn/hiddenlayer_0/biasIdentity"dnn/hiddenlayer_0/bias/part_0/read*
T0
v
dnn/hiddenlayer_0/BiasAddBiasAdddnn/hiddenlayer_0/MatMuldnn/hiddenlayer_0/bias*
T0*
data_formatNHWC
B
dnn/hiddenlayer_0/ReluReludnn/hiddenlayer_0/BiasAdd*
T0
�2
dnn/hiddenlayer_1/kernel/part_0Const*�2
value�2B�2(("�2��F=�����LE>-f\�\�!>*��<�.�̊@�e�>
=���ˌ��������=<�'�����8֓<ݾ >ޓ���Z>}W���C>��>F��<�O>F>��>x�)��=�U1��_�=�W�>��1焽P�=.����ֽf칽�w�>,�����%�r�>��J=82=����~˽�d����L>�3�=�%>>h,=��A>�2�=���=��C<D��=0��=��>>��x>        @)Q>z�ǽ�k�n>�9���<�A�0z�>Ї���$���!>�:�JD�> �b>�'R��>h�r�>pҽ�-�=��W=��<	>���:�����>P� >�j>t�,��Qb>�D2�[����)6=�ȝ�W�>�|��<\>�=>u��>z>�O>���=��i>ބ>x��@{)�8���a�t��q�>���HN�=䊽�f���Ք�BEO�7z*>�����>��>��˼H�>�>��3�e�c��@�=6�[���0���U��@�W	�>u�|�P����H������9I��/�=�7�>�<~>xB>��9>/��=B�>�RA=u�X�a�-�D ��œ�>�h%>��=��%>P����;��n=�\��??>�UN�M�~> ��?)��)ҽ��>��=�=���>�>K��Cu'���=Z��EG7=x&%>ܿ�=��>�Ͻ�&M���}��>P�8�V����*f�=�[=>���EH�>���=����.��>���=�ʨ='�.��=>y� ��(W��C����3�qQ.>����F<P=���>f3��5-���z>.B�>����h���E�!�pF��N�>��k��j�<�%>8��=�/>WJ���=^�=�r��$>�'>o��I��>�~�=��R�%�>9Ou> X�=��jL�=SWK>Q��=�bE=@�o>�6���R��V�=��`�"�u���$�J1]��>Y�>�}�=���%��0{>c>>�$>hSڻek��,v�a�D�X�+>���>w���    @ػ���>Шm>�>cp<o!���>��ڙ>vｼ�N>�(�>��<�d��="�==:5�=zQt���=vW#=q�f���<�î�A��;~\>    D���25>9S=���=���
_���>^L���/�L�}�r�>    p��=��2�@�>��;��B����=����~���=T���xM>&����$��~�	����G>�x>>>��н�����A>"xk;�hƼ�	>�s�<����NA�܄�9h�g���؝4=i���X���KB�=�X>*-�Sp�=l�=8>ۨD�ͨ�=�я������]>�>޼Y�= Uh>��;>��н�w>�J�>�գ��<>m�>���<؍P;��=\��<�X�<H�{>�"=�)~>d�b<�����h��_G�=�0/>iX2>��h>�$��ڒ>�!n��em> c�= �s>U_>�W���=]I��$N>���=�ϱ�#u�rȱ=��=^�V>�/\=I"��TI>%��4D�>xe=��/�~R =�Hʽp;�>e:��)G���`= �S���=A�I�h�>���E>L!P�t�=(ۚ��[��H4>H���p<>��2>��@��9<�ɜ���=>�[����g>0b8=��d�g6Z��kd>h��=�%��.>�Ƚf��,b>x�[�'�=��Q�El�0��x��Zr���>;�ϼl�>J�O>Pu>���=X[>�<��]>�>(�i���(�,��Bl���-�\�=ˤW>����f(>�@�� :+=�W��C�t�*��=Y�u�P��=d�����,>h3�<�����m�=�w1�C>�=\�
�H8㽳y[�ra$���H�*=N\>5�!>g��=��>��b�X��D�m>����Yν6>��>>!˽�v�"�<�e���ݽ�v���=�4��=@�X�~�<�2i>+���E�4��=$� >��K=����߾<8�z�o%H�H��v���lJ�� >H.���M�=$a��ý�?��    ��l�"<(��B���/�0�>@i�=��7�>;��r~>��<��>T+>ͽ�=�><>�$I�LH�
R1>�8�06 >I_N=�6�a�]�W�<lHT�	�	���H>�����:���=���=:�=	�U=K�%�!6ѽ���Q���~.��\=����2>�*>��E=�]�F/Ȼp�1>XK8>��>�U>�u>Tw>�Ƚ���<bXO���=�AG�0��=T<r>x>4�    ���XV���Q�<V\>z�?>2�2��]=Z>�⍽�[���>R%����g>0c���;�%>( ��\m �<W�=
�����R>f\�>p���	��4�[�Ddv>�W&�8���9�=H1*>�"�>	 >���=v��n<���9>��=���=��u���)=,$=���<>�#��f_�pYu�\��=[M�g�=�t�>6�=�L��h4��.
�|"�m�ۼ�s���u>`5��U�_���r>�ji>����]�=F͕=\�>��S=R=��=����>d�j>`��X�s��;�OA���4=��
>5�c��C��\�`����&��:>,�>�^I>�c���/>����>��o<�S���Y�=�5�Yȿ<]KU�X�P��>O�t>�6�=��ٽ��>V��J�o>Fn>"A޽Z=9>�$�~Ϡ��,�݁�>�N��^�.�ѩ�=1%�=$g&��os>׆�<��b�e��� }[��E�>��R>ڂC�$�l����~���&����/S�2������?F>汧=�ߦ>y�֨�=W�V>��(XV���ҽ$��:A>��L�z[b>���=�����i�pn��"@X���f>�L~�0�j��s)�&uJ��yA��P>    "�>v�>�=��؄:P�ȼ��&�U:�8�����o�    �D=��m���a���R>ȟL��[��9�<h�=�.��p��=nvD�R<_�S>,~���Խ�=��Լ@�C��>x=���Z�=Cil:8\�=��>��>]�=9_�����=���&JF�]?�gv������<�,=j4>NcM<!:�>*�.>    � �;FX�ҢA>������>,�H��q��v/�$�j=��F��w�Oy�o�B�1�h���=e>o>�L�=H���μZ�ۼ�Ξ�����c@>XO9��\\>��5��~�=vM�    j%�>0>6h*��hμ��=6KD���L�ȳ:�V�n�N> F��(���w9>h��<ԮL����=Ȟ�=$�n>�E4=k
M���=|X,�8�ὝP���yK�Ɵ6>n>`h,=B��P%%>5G�^[����� >O\{<V�c>U�=6=��B<��;�ٽ�Ń�N�>�h��_h>����S�����>.v���[�M%�>@��>�BN���һho>�PQ=?'�>A��������l>XL9=���>hn��p�=�i���	5�꫕=?�c��2>�m>��=r�>DL>���>����>�·�Ԑ~=hc>��m=��A�=8��=ٲ����<�Ub>����.�f�(�"�>���=^���-�f^��Z)>���ð��uj=rt�<�<�T�=@�;=�Q`>Y6{=�9Z>7�#>@��=�(u>*�>�h4>M}[><��s��=����{�=����T>��:<�C�>Ϣ%>�Rx��f��c��>���!�W��@Z��x�=~�>�r�n�F}�>����X�>�dZ�WP>�k>ˣI=�2>��ճ0>�Iz�S>ս<:4�k*>ּ=�㏾rb�Q�a>}Ǹ=��[�`�=�!	>�ԉ������p�>8�s�@��=Qo��Ĵ1����    �V>��e����(�绦��9�>`t�x�8>��=���=9��6���c:>�1�>Hp.>�=����_8J>�-�f�=ZX�74O=�8}�@���muc>�ұ�\�+�p>�y<��2�>��=��>DT��Po>1烾J.����<����<�+=�J�=� V���s���=�}B=��F>�[k�.�ս���=�"�=e8�� b���ӽ��v>n��H
���	�m��=�2�K'k>����GE�ʊ�=U�/��~X��n��-õ�瓕���D>w�ʽ*��=LZ|��{��I�=�O�>�>���:d̽��8�XU]>��=��O��NT��>�H(��]Z�ȏ�<ķ�=h� =X#/=�^>��g�T�@>�/>S�\�<���;�v���Z潄�.�;G8;j2X���g>^瞽    @)m>�=o��큽�u/>�9�=eXm��;��=�����Z>�Eܼ��{>`��=�u}��Ov�    ��Q>^�B>jLN>����&�z�-�A���v=R�8>ΞU���>xWX>�g��@N>��m>F0��D�l>�h>��>��<��=	(>��> ѵ��w�=�Ʌ�    t�v�@ȁ>��2���>�r����=Iu��HR�<dg����>�>�������ӆ)>�L�2�ҽ��{��_>;�6>�}����>�FS�¨2=��p>�n�=�%�=wV:�K��(?�=�T�>�'P>    \u��J�xk`>�P[>C�\���\��8Q�	��<��7=#�,=�`m>#wL�"�>�I�= ��=�]�N�,�A�s=p=8����?>T�ټ5��PX�ZR|S���>޶<>s�8�    |��<l�@>,١=�c0�ę^>�X���K�=�sa=<>˽�݃>���_R>6�]�s�=��8�,`>���=��>�G��ۇv���Խ�>=-�>����TTU>G��vB���>I�=��Q�L��=4x^�my��١'��M=��[����wЂ��l>��3��a=�͂�p��<��� o��$>�Ϋ��~輽�\��5��M��?>�&>�|�=�2Լhyn���>�B���:>�`>���=��=�V>ΠY���>�u��^>���:��/��༠��=��<�=�7;>�[=�Ht�r9k>̥�=I��˻���/>��[��V�w����=X��=݊�)
=6R�>�,����>�X>��=�����]�CN�    �쭼���?5�����1m�\ڕ>C>����=d2�?��^�<<<��=�z>�6>9�w�+U��ǒ�T!޽��=�R�6h��������5���傾���<0��<,�>N�S=6w�>lKj>���=�_!>C�m�c��=~�=4˞�q�PQ�;	ս9jս���=��I�Ur��(�>X	@>Jj<��Qb>�N�4X��/C�,�M�Ƚ�=�Lݼ/L�<(�f>    ,�`��)F�G�x��<    (������ye�*-h�jg���d���&2�    �r��q>�'M=~t��|��.�<Sh�y+l�:�->����6=�<�t>4�=��8=�M���X�ҍ:������W��,�����M�i���=�� ��t�>P!>X*N�,���	1���o��i���Z5�V1�#u�B@>��)> ��v7T�8}
>�%=`�v>y���.�=3M��:>���>��]�2fI�#�i���ν�r�4��=,Qi>�����yN�9�7���!>h#����&> �;��`w����5>4ƽda(:�邾[UӼ�Zr��
�;%|��٦��w�OdN��8�=�GD>��h><LB>N!���Q�ۻL�>(�R=N�Z��C�H�B���=�����+�cÐ=Q�r=S�=�c=z])�|e�=�T��`_>�q�>{iĻ'��hf�>g��j6>>H�@���=�#>�5�<�ݲ=��	>a�>� >��3�%鼂��>	�k���B>J�Q<�^���й>c��Sg��$N>ʁ����=�p*�4?L>�>6Hm>T1�=�lp�>)���L��$����>�Y>���=PW���㙽�m�By=6lu�hX�<�>4�I>�	K�כu���>�u,��>S���1��>C=��>ƃ\<�`_�$ ��<y�U��˙=�ʒ>��>y�H��~=`���������=T�	>��+>�0�Ɖ>��/	�x�<�Z>�Q>���_;c<���<�zU�X��=��=~^��[6���R�!a~>���=���(>
h*>uo)>T����n>��6=p�=-��=�2>�n+��Ub�ޑ&=��>5(==��>[��82">8�<m~�+G��g�N��8>�f#=����E>\�9>��j>�gX>*��=��f��mY��T���XM=�=��нͶN>Ԃ�� ��@�>��r>�B+>
Ue���l��wx=�ҟ>x�>��2�'�(���>�սk1]>Lޱ�i�
=�$�>Q�	>�<�>;r�Ʊz���<ݓ�����=he�d�X>��)>���<��<R��p�2=z-z>w҂�*
dtype0
�
$dnn/hiddenlayer_1/kernel/part_0/readIdentitydnn/hiddenlayer_1/kernel/part_0*
T0*2
_class(
&$loc:@dnn/hiddenlayer_1/kernel/part_0
�
dnn/hiddenlayer_1/bias/part_0Const*�
value�B�("��O�=���ǌ�=�c��    =�ܻ��    �{~=�[Z�dƼ0c�    �*�=C���    � �=X��=    
]S�rRU�V�=ׇ�=A� ��b�=��=    C��=�o����=�L6�����v�����᳙�8����'2���=���*
dtype0
�
"dnn/hiddenlayer_1/bias/part_0/readIdentitydnn/hiddenlayer_1/bias/part_0*0
_class&
$"loc:@dnn/hiddenlayer_1/bias/part_0*
T0
S
dnn/hiddenlayer_1/kernelIdentity$dnn/hiddenlayer_1/kernel/part_0/read*
T0
�
dnn/hiddenlayer_1/MatMulMatMuldnn/hiddenlayer_0/Reludnn/hiddenlayer_1/kernel*
transpose_b( *
T0*
transpose_a( 
O
dnn/hiddenlayer_1/biasIdentity"dnn/hiddenlayer_1/bias/part_0/read*
T0
v
dnn/hiddenlayer_1/BiasAddBiasAdddnn/hiddenlayer_1/MatMuldnn/hiddenlayer_1/bias*
T0*
data_formatNHWC
B
dnn/hiddenlayer_1/ReluReludnn/hiddenlayer_1/BiasAdd*
T0
�
dnn/logits/kernel/part_0Const*�
value�B�("��˯>ESp�)�=k�ٽtʚ��F�>�����r��
�>���=���%�g���i>��>��U������ư>TK3>�g=Qɞ��뒽��	>��=�r��7��>�v�>h2�=���>�W���>+���;Q���|캙�e�����;���S���>]��*
dtype0
y
dnn/logits/kernel/part_0/readIdentitydnn/logits/kernel/part_0*
T0*+
_class!
loc:@dnn/logits/kernel/part_0
G
dnn/logits/bias/part_0Const*
dtype0*
valueB*{yr=
s
dnn/logits/bias/part_0/readIdentitydnn/logits/bias/part_0*
T0*)
_class
loc:@dnn/logits/bias/part_0
E
dnn/logits/kernelIdentitydnn/logits/kernel/part_0/read*
T0
u
dnn/logits/MatMulMatMuldnn/hiddenlayer_1/Reludnn/logits/kernel*
T0*
transpose_a( *
transpose_b( 
A
dnn/logits/biasIdentitydnn/logits/bias/part_0/read*
T0
a
dnn/logits/BiasAddBiasAdddnn/logits/MatMuldnn/logits/bias*
T0*
data_formatNHWC�K
�K
.
_make_dataset_fb900292
RepeatDataset��?
&TensorSliceDataset/tensors/component_0Const*�>
value�>B�>("�>      �?      @      �?       @                                                       @              @      �?                                                                                              �?      =@      @                                       @      �?              �?      @      &@      @                                                                                              �?     �A@      @                      @      �?       @      �?                      @      @                                                                                                      @      6@      @       @      �?      @       @       @      @      @      �?              �?                                                                                                      @     �D@      $@      �?      �?      �?      @       @      "@      "@       @       @                                                                                                              @      B@      &@      @      @      @      @       @       @      @       @                      �?                                                                                               @      2@      &@      @      @      @      @      @       @      �?              �?                                                                                                              @      4@      @      �?                              @      @      @      @       @      @       @      �?              �?                                                                      @      @@      @               @               @      �?      &@      *@      $@      @      @      �?      �?                                                                                      @     �R@      6@      "@      &@      @      @      @      (@      @       @      *@      .@      @      @       @                                                      �?                      @      &@       @                                                                       @      @                                                                                                      @      <@      @      @              �?              �?                              @      @       @                                                                                              �?      ?@      &@      �?       @      @       @      @       @       @       @      $@      @      �?      �?              �?                                                                      �?      7@      "@                      �?       @      @      @      @       @      �?      @              �?                                                                                      @      A@       @      �?       @      @      @      @      .@      @      @       @      �?                                                                                                      @     �G@      $@      �?               @       @      @      @      @       @      @      �?      �?                                                                                              @      1@       @                       @      @      @      @      @      @      @              �?                                                                                              @      A@      2@      @              �?      @      "@      @      @       @       @      �?      �?      �?                                                                                      @      6@       @              �?       @      @      @      @      @      �?       @                                                                                                              @      @@      *@      @      �?       @      @      *@      @      @      �?      �?      �?                                                                                                      @      0@      @              �?       @      @      @      @       @       @      �?      @                                                                                                      @     �F@      @      �?       @      @      @      "@      @      @      @      �?                                                                                                              �?      6@      &@       @              @      @      @      *@      &@      @      @      @      �?      �?                                                                                       @      8@       @       @              @      @      @      @      @      �?                                                                                                                      @      D@      (@       @      �?              �?      @      &@      @      �?      @      �?      �?                                                                                               @      B@       @                              @      @      2@      @      @              @       @      �?                                                                                       @     �A@      .@      @              �?      �?      @      "@      @      @      �?      @       @                                                                                              @      7@      @       @                      @       @      $@       @      �?      �?      �?                                                                                                      @      @@      ,@      @      �?      @      @      $@       @      @                                                                                                                               @      ;@       @      �?                       @      @       @      @      @      @                                                                                                               @      8@      .@              �?               @       @      ,@       @      �?      @      @      �?                                                                                               @      1@      @      @      @      @      @      @      @      @       @      @      �?               @                                                                                       @      ?@      0@      @      @      @      @      �?      @       @      �?              @                      �?                                                                              @      0@      @      @      @       @      @      �?      @      "@      �?               @      �?                                                                                              @     �D@      @      �?       @      @      @      @      $@      @              �?      �?                                                                                                      "@      @@      @      @      �?       @      �?      @      ,@       @      @              @                                                                                                      �?      C@      @      �?       @      �?       @       @      @      @      �?                                                                                                                      @      @@      4@                      �?       @      @      (@      "@       @                      �?                                                                                               @     �F@       @      @      �?      @      �?      @      (@      @      @                                                                                                                      @      =@      @              �?                      @      @      @      @                                                                                                                *
dtype0�
&TensorSliceDataset/tensors/component_1Const*�
value�B�	("�       :       <       7       Y       V       9       ;       X       �              :       Q       ;       X       X       <       Y       8       Y       ;       X       Y       :       X       Z       Y       :       Y       ;       W       8       S       9       W       Y       <       Y       Z       ;       *
dtype0	�
TensorSliceDatasetTensorSliceDataset/TensorSliceDataset/tensors/component_0:output:0/TensorSliceDataset/tensors/component_1:output:0*
Toutput_types
2	*
output_shapes

:: D
ShuffleDataset/buffer_sizeConst*
value	B	 R(*
dtype0	=
ShuffleDataset/seedConst*
value	B	 R *
dtype0	>
ShuffleDataset/seed2Const*
value	B	 R *
dtype0	�
ShuffleDatasetShuffleDatasetTensorSliceDataset:handle:0#ShuffleDataset/buffer_size:output:0ShuffleDataset/seed:output:0ShuffleDataset/seed2:output:0*
output_shapes

:: *
reshuffle_each_iteration(*
output_types
2	A
BatchDataset/batch_sizeConst*
value	B	 R*
dtype0	�
BatchDatasetBatchDatasetShuffleDataset:handle:0 BatchDataset/batch_size:output:0*
output_types
2	*5
output_shapes$
":���������:���������F
RepeatDataset/countConst*
valueB	 R
���������*
dtype0	�
RepeatDatasetRepeatDatasetBatchDataset:handle:0RepeatDataset/count:output:0*
output_types
2	*5
output_shapes$
":���������:���������"'
RepeatDatasetRepeatDataset:handle:0" 